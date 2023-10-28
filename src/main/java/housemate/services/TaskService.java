package housemate.services;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import housemate.constants.Enum.TaskStatus;
import housemate.constants.ScheduleStatus;
import housemate.entities.Schedule;
import housemate.entities.Task;
import housemate.models.TaskViewDTO;
import housemate.repositories.ScheduleRepository;
import housemate.repositories.StaffRepository;
import housemate.repositories.TaskReposiotory;

public class TaskService {

	@Autowired
	ScheduleRepository scheduleRepo;

	@Autowired
	TaskReposiotory taskRepo;
	
	@Autowired
	StaffRepository staffRepo;
	
	ModelMapper mapper = new ModelMapper();

	private final ZoneId dateTimeZone = ZoneId.of("Asia/Ho_Chi_Minh");

	List<Task> taskList;

	// create task
	// update task
	// remove task
	// getAllTaskByStaff
	// getAllTaskBySchedule
	// taskEmployeeSelection


	@Scheduled(cron = "0 0 23 * * *") //call this at every 23 PM
	public void createTaskAutomaticByScheduleTime() {
		// automatic scanning schedule at 12:00PM
		// get all schedule and filter which schedule will coming up in next 7 day
		try {
			List<Schedule> schedules = this.upComingScheduleInNext7Days();
			// generate task for these schedule
			for (Schedule schedule : schedules)
				this.createTaskForm(schedule);
			// TODO: send notification of new task list
		}catch (Exception e) {
			e.printStackTrace();
		}
	
	}

	public List<Schedule> upComingScheduleInNext7Days() {
		List<Schedule> schedules = scheduleRepo.findAllScheduleUpComing(7);
		return schedules;
	}

	// create task form
	public Task createTaskForm(Schedule schedule) {
		// check if the task for this schedule have created befored by system
		Task task = taskRepo.findByServiceScheduleId(schedule.getScheduleId());
		if (task == null) {
			task.setServiceScheduleId(schedule.getScheduleId());
			task.setCreatedAt(LocalDate.now(dateTimeZone));
			task.setTaskStatus(TaskStatus.PENDING_APPLICATION);
			task.setStaffId(null);
			task.setReceived_at(null);
			scheduleRepo.findById(schedule.getScheduleId()).ifPresent(s -> s.setStatus(ScheduleStatus.PROCESSING));
			taskRepo.save(task);
			//TODO: trigger the task countdown
		}
		return task;
	}
	
	public ResponseEntity<?> getAllTaskAvailable(Optional<Integer> page, Optional<Integer> size) {
		int pageNo = page.orElse(0);
		int pageSize = size.orElse(9);
		if (pageNo < 0 || pageSize < 1)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Page number starts with 1. Page size must not be less than 1");

		// setting sort
		Sort sort;
		sort = Sort.by(Sort.Direction.ASC, "createdAt");

		Pageable pagableTaskList = pageNo == 0 ? PageRequest.of(0, pageSize, sort)
				: PageRequest.of(pageNo - 1, pageSize, sort);

		Page<Task> taskList = taskRepo.findAllByTaskStatus(TaskStatus.PENDING_APPLICATION, pagableTaskList);
		
		Function<Task, TaskViewDTO> convertInToTaskViewDTO = task -> {
			TaskViewDTO taskView = new TaskViewDTO();
			taskView = mapper.map(task, TaskViewDTO.class);
			taskView.setShedule(scheduleRepo.findById(task.getServiceScheduleId()).orElse(null));
			taskView.setStaff(staffRepo.findById(task.getStaffId()).orElse(null));
			return taskView;	
		};
		Page<TaskViewDTO> taskViewList = taskList.map(convertInToTaskViewDTO);
		
		return ResponseEntity.ok(taskList);
	}
	
	//TODO
	public void TaskTimeUpTrigger(Task task) {
		
	}
	

}
