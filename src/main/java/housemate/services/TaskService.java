package housemate.services;

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

import housemate.constants.ImageType;
import housemate.constants.Enum.TaskStatus;
import housemate.entities.Image;
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

public class TaskService {

	@Autowired
	TaskReposiotory taskRepo;
	
	@Autowired
	TaskBuildupService taskService;
	
	@Autowired
	ScheduleRepository scheduleRepo;

	@Autowired
	StaffRepository staffRepo;
	



	// VIEW TASK PENDING APPLICATION
	public ResponseEntity<?> getAllTaskInPendingApplication(Optional<Integer> page, Optional<Integer> size) {
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
			return taskService.convertIntoTaskViewDTOFrTask(task);
		};
		Page<TaskViewDTO> taskViewList = taskList.map(convertInToTaskViewDTO);

		return ResponseEntity.ok(taskList);
	}
	
	//VIEW TASK UP COMING WORING BY STAFF
	public ResponseEntity<?> getAllTaskUpComingDoing(int staffId){
		Staff staff = staffRepo.findById(staffId).orElse(null);
		if(staff == null) 
			return ResponseEntity.badRequest().body("Staff not exists");
		
		
		return null;
	}

	// UPDATE TASK TO CHANGE THE TIME
	// UPDATE TASK FOR NEW NOTE

}
