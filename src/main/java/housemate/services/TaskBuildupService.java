package housemate.services;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Collections;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import housemate.constants.Enum.TaskStatus;
import housemate.constants.ImageType;
import housemate.constants.ScheduleStatus;
import housemate.entities.Image;
import housemate.entities.Schedule;
import housemate.entities.Staff;
import housemate.entities.Task;
import housemate.entities.UserAccount;
import housemate.models.TaskViewDTO;
import housemate.models.TaskViewDTO.Customer;
import housemate.repositories.ImageRepository;
import housemate.repositories.ScheduleRepository;
import housemate.repositories.StaffRepository;
import housemate.repositories.TaskReposiotory;
import housemate.repositories.UserRepository;
import housemate.responses.TaskRes;
import housemate.responses.TaskRes.TaskMessType;
import jakarta.transaction.Transactional;

@Service
public class TaskBuildupService implements ITaskService{

	@Autowired
	ScheduleRepository scheduleRepo;

	@Autowired
	TaskReposiotory taskRepo;
	
	@Autowired
	StaffRepository staffRepo;
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	ImageRepository imgRepo;
	
	ModelMapper mapper = new ModelMapper();


	private final ZoneId dateTimeZone = ZoneId.of("Asia/Ho_Chi_Minh");
	
//======CREATE TASK======//
	@Override
	@Scheduled(cron = "0 0 0 * * *", zone = "Asia/Ho_Chi_Minh") // call this at every 24:00 PM
	public List<Task> createTasksOnUpcomingSchedulesAutoByFixedRate() {
		List<Task> taskList = new ArrayList<>();
		try {
			// get all schedule and filter which schedule will coming up in tomorrow
			List<Schedule> schedules = scheduleRepo.findAllScheduleInUpComing(ScheduleStatus.PROCESSING, 1);
			// generate task for these schedule
			if (!schedules.isEmpty()) {
				for (Schedule schedule : schedules)
					taskList.add(this.createTask(schedule));
			} else 
				taskList = Collections.EMPTY_LIST;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return taskList;
	}

	@Override
	public List<Task> createTaskOnUpComingSchedule(Schedule newSchedule) {
		List<Schedule> schedules = scheduleRepo.findAllByParentScheduleAndInUpComing(ScheduleStatus.PROCESSING, 1, newSchedule.getScheduleId());
		List<Task> taskList = new ArrayList<>();
		if (!schedules.isEmpty()) {
			for (Schedule theSchedule : schedules)
				taskList.add(this.createTask(newSchedule));
		}else 
			taskList = Collections.EMPTY_LIST;
		
		return taskList;
	}

	@Override
	@Transactional
	public Task createTask(Schedule schedule) {
		// Check if the task for this schedule have created before by system
		// This will have to return only ONE OR NULL task for this schedule with the taskStatus differ from CANCELLED
		Task task = taskRepo.findExistingTaskForSchedule(schedule.getScheduleId());
		Task savedTask = null;
		try {
			if (task == null) {
				task.setScheduleId(schedule.getScheduleId());
				task.setParentScheduleId(schedule.getParentScheduleId());
				task.setCreatedAt(LocalDateTime.now(dateTimeZone));
				task.setTaskStatus(TaskStatus.PENDING_APPLICATION);
				task.setStaffId(null);
				task.setReceivedAt(null);
				scheduleRepo.findById(schedule.getScheduleId()).ifPresent(s -> s.setStatus(ScheduleStatus.PROCESSING));
				savedTask = taskRepo.save(task);
			}
		} catch (Exception e) {
			e.printStackTrace();
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
		return savedTask;
	}
	
	//======VIEW TASK======//	
	
	public TaskViewDTO convertIntoTaskViewDTOFrTask(Task task) {
		TaskViewDTO taskView = new TaskViewDTO();
		
		List<Image> staffAvatar =  imgRepo.findAllByEntityIdAndImageType(task.getStaffId(), ImageType.AVATAR).orElse(Collections.EMPTY_LIST);
		task.getStaff().setAvatars(staffAvatar);
		
		UserAccount customerInfoFrAcc = userRepo
				.findById(scheduleRepo.findById(task.getScheduleId()).orElse(null).getCustomerId())
				.orElse(null);
		List<Image> customerAvatar =  imgRepo.findAllByEntityIdAndImageType(customerInfoFrAcc.getUserId(), ImageType.AVATAR).orElse(Collections.EMPTY_LIST);
		Customer customer = mapper.map(customerInfoFrAcc, Customer.class);
		customer.setAvatar(customerAvatar);
		
		taskView = mapper.map(task, TaskViewDTO.class);
		taskView.setCustomer(customer);
		
		
		return taskView;
	}
	
	
	
	
	
	
	@Override
	public Task cancelTaskByCustomer(Schedule schedule, Optional<String> cancelReason) {
		//This will have to return only ONE OR NULL task for this schedule with the taskStatus differ from CANCELLED
		Task taskToBeCancelled = taskRepo.findExistingTaskForSchedule(schedule.getScheduleId());
		String taskNoteMess = cancelReason.orElse("The customer has cancelled the task !");
		if(taskToBeCancelled != null) {
			taskToBeCancelled.setTaskStatus(TaskStatus.CANCELLED_BY_CUSTOMER);
			taskToBeCancelled.setTaskNote(taskNoteMess);
			schedule.setStatus(ScheduleStatus.CANCEL);
			//TODO: SEND NOTIFICATION IN ANOTHER FUNCTION WHEN CALLBACK THIS FUNCTION
		}
		//TODO: SUBSTRACT THE CUSTOMER RELIABILITY
		return taskToBeCancelled;
	}
	
	@Override
	public TaskRes<?> updateTaskOnScheduleChangeTime(Schedule oldSchedule, Schedule newSchedule) {
		//TODO: Consider the condition to update task
		Task cancelledTask = null;
		Task updatedTask = null;
		LocalDateTime timeNow = LocalDateTime.now(dateTimeZone);
		LocalDateTime timeScheduleStart = oldSchedule.getStartDate();
		long hours = ChronoUnit.HOURS.between(timeNow, timeScheduleStart);
		if(hours <= 3) {
			return TaskRes.build(updatedTask, TaskMessType.REJECT_UPDATE_TASK,
					"Unsufficent profiency score. Comback to apply if the task not found any staff before the time schedule starts 3 hours");
		}
		cancelledTask = cancelTaskByCustomer(oldSchedule, Optional.of("The customer has changed the time !"));
		updatedTask = this.createTask(newSchedule);
		// TODO: SEND NOTIFICATION TO STAFF WOULD LIKE TO FOLLOW THE CHANGE IN ANOTHER FUNCTION WHEN CALLBACK THIS FUNCTION
		if (updatedTask == null)
			return TaskRes.build(updatedTask, TaskMessType.UPDATE_FAILED, "Something Errors, Updated Failed !");
		//TODO: SUBSTRACT THE CUSTOMER RELIABILITY
		return TaskRes.build(updatedTask, TaskMessType.OK, "Updated successfully !");	
	}
	
	//======APPROVE STAFF======
	
	@Override
	public boolean approveQualifiedStaff(Staff staff) {
		// if staff < 40 -> disapprove 0 message : not sufficient proficient score -
		// come back for applying if the task not found any staff before the schedule 3 hours
		if(staff.getProfiencyScore() > 40) {
			
		}
		return false;
	}	
	
	//=== CLOSE TASK===//
	
	//Close task when found staff
	public void closeTaskWhenFoundStaff(Task task) {
		if(task.getStaffId() != null) {
			task.setTaskStatus(TaskStatus.PENDING_WORKING);
		}	
		
	}
	
	@Override
	public void closeTaskAutomaticWhenTimesUp(Task task, LocalDateTime timeEnd) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	







//	@Override
//	public List<Task> createTaskOnNewScheduleHasCycle(Schedule papaSchedule) {
//		List<Task> newTaskListForCycleSchedule = new ArrayList<>();
//		if(papaSchedule.getCycle().equals(Cycle.ONLY_ONE_TIME)) {
//			Task task  = this.createTask(papaSchedule);
//			if(task != null) {
//				 newTaskListForCycleSchedule.add(task);
//				 return newTaskListForCycleSchedule;
//			}
//		}
//		else {
//			List<Schedule> schedules = scheduleRepo.findAllByParentScheduleAndInUpComing(
//					ScheduleStatus.PROCESSING, 7, papaSchedule.getParentScheduleId());
//			if(!schedules.isEmpty()) {
//				for (Schedule childSchedule : schedules)
//					newTaskListForCycleSchedule.add(this.createTask(papaSchedule));
//			}
//		}
//		return newTaskListForCycleSchedule;
//	}
	
	
//	@Override
//	public List<Task> cancelRepetitiveTaskByCustomer(Schedule papaSchedule) {
//		List<Task> cancelledTaskList = new ArrayList<>();
//		List<Task> tasksToBeCancelled = taskRepo
//				.findAllExistingRepetitiveTasksOfPapaSchedule(papaSchedule.getParentScheduleId());
//		if (!tasksToBeCancelled.isEmpty()) {
//			for (Task cycleTask : tasksToBeCancelled) {
//				Task cancelledTask = new Task();
//				cancelledTask = cancelTaskByCustomer(scheduleRepo.findById(cycleTask.getScheduleId()).orElse(null),
//						Optional.of("The customer has cancelled the task !"));
//				if (cancelledTask != null)
//					cancelledTaskList.add(cancelledTask);
//			}
//		}
//		return cancelledTaskList;
//	}

}
