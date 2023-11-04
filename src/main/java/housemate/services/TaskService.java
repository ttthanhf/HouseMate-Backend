package housemate.services;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import housemate.constants.ScheduleStatus;
import housemate.constants.ServiceTaskConfig;
import housemate.constants.Enum.TaskReportType;
import housemate.constants.Enum.TaskStatus;
import housemate.constants.ImageType;
import housemate.constants.Role;
import housemate.entities.Customer;
import housemate.entities.Image;
import housemate.entities.Schedule;
import housemate.entities.Staff;
import housemate.entities.Task;
import housemate.entities.TaskReport;
import housemate.entities.UserAccount;
import housemate.models.TaskReportNewDTO;
import housemate.models.TaskViewDTO;
import housemate.repositories.CustomerRepository;
import housemate.repositories.ImageRepository;
import housemate.repositories.ScheduleRepository;
import housemate.repositories.StaffRepository;
import housemate.repositories.TaskReportRepository;
import housemate.repositories.TaskReposiotory;
import housemate.repositories.UserRepository;
import housemate.responses.TaskRes;
import housemate.responses.TaskRes.TaskMessType;
import housemate.utils.AuthorizationUtil;
import jakarta.servlet.http.HttpServletRequest;
import static housemate.constants.ServiceTaskConfig.*;


@Component
public class TaskService {

	@Autowired
	TaskReposiotory taskRepo;
	
	@Autowired
	TaskBuildupService taskBuildupServ;
	
	@Autowired
	ScheduleRepository scheduleRepo;

	@Autowired
	StaffRepository staffRepo;
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	TaskReportRepository taskReportRepo;
	
	@Autowired
	ImageRepository imgRepo;
	
	@Autowired
	CustomerRepository customerRepo;
	
	@Autowired
    AuthorizationUtil authorizationUtil;
	
	@Autowired 
	ServiceTaskConfig taskConfig;
	
	private final ZoneId dateTimeZone = ZoneId.of("Asia/Ho_Chi_Minh");
	
	private static final Logger log = LoggerFactory.getLogger(TaskService.class);


	// VIEW TASK PENDING APPLICATION
	public ResponseEntity<?> getAllTaskInPendingApplication(
			Optional<Sort.Direction> orderByCreatedDirection,
			Optional<Integer> page,
			Optional<Integer> size) {		
		int pageNo = page.orElse(0);
		int pageSize = size.orElse(9);
		Sort.Direction direction = orderByCreatedDirection.orElse(Direction.ASC);
		if (pageNo < 0 || pageSize < 1)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Page number starts with 1. Page size must not be less than 1");

		// setting sort and pageable
		Sort sort = Sort.by(direction, "createdAt");
		Pageable pagableTaskList = pageNo == 0 ? PageRequest.of(0, pageSize, sort)
				: PageRequest.of(pageNo - 1, pageSize, sort);
		Page<Task> taskList = taskRepo.findAllByTaskStatus(TaskStatus.PENDING_APPLICATION, pagableTaskList);
		
		Page<TaskViewDTO> taskViewList = Page.empty(pagableTaskList);
		if (!taskList.isEmpty()) {
			Function<Task, TaskViewDTO> convertInToTaskViewDTO = task -> {
				return taskBuildupServ.convertIntoTaskViewDtoFromTask(task);
			};
			taskViewList = taskList.map(convertInToTaskViewDTO);
		}
		return ResponseEntity.ok(taskViewList);
	}
	
	//VIEW TASK UP COMING WORING BY STAFF
	public ResponseEntity<?> getAllTaskForStaffByTaskStatus(HttpServletRequest request, @Nullable TaskStatus taskStatus,
			Optional<Integer> page, Optional<Integer> size) {

		int staffId = authorizationUtil.getUserIdFromAuthorizationHeader(request);

		Staff staff = staffRepo.findById(staffId).orElse(null);
		if (staff == null)
			return ResponseEntity.badRequest().body("Staff not exists");

		int pageNo = page.orElse(0);
		int pageSize = size.orElse(9);
		if (pageNo < 0 || pageSize < 1)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Page number starts with 1. Page size must not be less than 1");

		// setting sort and pageable
		Sort sort = Sort.by(Sort.Direction.DESC, "receivedAt");

		Pageable pagableTaskList = pageNo == 0 ? PageRequest.of(0, pageSize, sort)
				: PageRequest.of(pageNo - 1, pageSize, sort);

		Page<Task> taskListForStaff = taskRepo.findAllByTaskStatusAndStaffId(staffId, taskStatus, pagableTaskList);
		Page<TaskViewDTO> taskViewListForStaff = Page.empty(pagableTaskList);
		
		if (taskListForStaff.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Empty List !");
		
		Function<Task, TaskViewDTO> convertInToTaskViewDTO = task -> {
			return taskBuildupServ.convertIntoTaskViewDtoFromTask(task);};
		taskViewListForStaff = taskListForStaff.map(convertInToTaskViewDTO);

		return ResponseEntity.ok(taskViewListForStaff);
	}
	
	//VIEW TASK IN DETAILS
	public ResponseEntity<?> getTaskInDetails(int taskId) {
		Task task = taskRepo.findById(taskId).orElse(null);
		if (task == null) 
			return ResponseEntity.badRequest().body("Task not exists");
		TaskViewDTO taskViewInDetails = taskBuildupServ.convertIntoTaskViewDtoFromTask(task);
		return ResponseEntity.ok().body(taskViewInDetails);
	}
	
	public ResponseEntity<?> getTaskViewInDetailsForCustomerByScheduleId(HttpServletRequest request, int scheduleId){
		Schedule schedule = scheduleRepo.findById(scheduleId).orElse(null);
		if(schedule == null)
			return ResponseEntity.badRequest().body("Schedule not exists");	
		int scheduleOwner = authorizationUtil.getUserIdFromAuthorizationHeader(request);
		Role role = Role.valueOf(authorizationUtil.getRoleFromAuthorizationHeader(request));
		if(!role.equals(Role.ADMIN) && !(role.equals(Role.CUSTOMER) && scheduleOwner == schedule.getCustomerId()))
			return ResponseEntity.badRequest().body("You are not the owner of this schedule !");
		
		long dayDiff = ChronoUnit.DAYS.between(LocalDateTime.now(dateTimeZone), schedule.getStartDate());
		Task task = taskRepo.findExistingTaskForSchedule(scheduleId);
		
		if(schedule.getStatus().equals(ScheduleStatus.PROCESSING) && task == null && dayDiff > 1)
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No task has been scheduled for this schedule");
		else if(schedule.getStatus().equals(ScheduleStatus.CANCEL) && task == null)
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No task for the schedule has been cancelled");
		else if(task == null)
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Not found any task for this schedule");
	
		TaskViewDTO taskViewDto = taskBuildupServ.convertIntoTaskViewDtoFromTask(task);
		return ResponseEntity.ok().body(taskViewDto);
	}
	
	
	//CREATE TASK BY CUSTOMER
	public ResponseEntity<?> createNewTask(HttpServletRequest request, int scheduleId){
		Schedule schedule = scheduleRepo.findById(scheduleId).orElse(null);
		int customerIdRequestCreate = authorizationUtil.getUserIdFromAuthorizationHeader(request);
		
		if(schedule == null)
			return ResponseEntity.badRequest().body("Schedule not exists to create !");
		if(customerIdRequestCreate != schedule.getCustomerId())
			return ResponseEntity.badRequest().body("You are not the owner of this schedule !");
		if(schedule.getStatus().equals(ScheduleStatus.CANCEL) && taskRepo.findByScheduleIdAndTaskStatus(scheduleId, TaskStatus.CANCELLED_CAUSE_NOT_FOUND_STAFF) != null)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					"This task has been cancelled because not found any staff for your schedule. Let create another schedule if you want to continue");
		if(schedule.getStatus().equals(ScheduleStatus.CANCEL)) 
			return ResponseEntity.badRequest().body("Can not create task because you have cancelled this schedule !");
		
		if(taskRepo.findExistingTaskForSchedule(scheduleId) != null)
			return ResponseEntity.ok().body("This task has already created !");

		List<Task> task = taskBuildupServ.createTaskOnUpComingSchedule(schedule);
		if(task.isEmpty()) 
			return ResponseEntity.status(HttpStatus.OK).
					body("Because the schedule is far away from now. Please waiting when the schedule coming - the task will be created by the system !");
		List<TaskViewDTO> taskViewDtoList = task.stream().map(x -> taskBuildupServ.convertIntoTaskViewDtoFromTask(x)).collect(Collectors.toList());
		return ResponseEntity.ok().body(taskViewDtoList);
	}
	
	//CANCEL TASK
	public ResponseEntity<?> cancelTask(HttpServletRequest request, int scheduleId) {
		taskConfig.setting();
		int userIdRequestCancel = authorizationUtil.getUserIdFromAuthorizationHeader(request);
		
		Schedule scheduleToBeCancelled = scheduleRepo.findById(scheduleId).orElse(null);
		if(scheduleToBeCancelled == null)
			return ResponseEntity.badRequest().body("Schedule not exists to cancel");

		UserAccount userCancel = userRepo.findByUserId(userIdRequestCancel);
		if(userCancel == null) 
			return ResponseEntity.badRequest().body("User not found to allow cancel !");


		TaskRes taskRes = null;
		Task taskToBeCancelled = taskRepo.findExistingTaskForSchedule(scheduleId);
		Role role = userCancel.getRole();
		Customer customer = customerRepo.findById(userCancel.getUserId()).orElse(null);
		LocalDateTime timeStartOfScheduleToBeCancel = scheduleToBeCancelled.getStartDate();
		LocalDateTime timeNow = LocalDateTime.now(dateTimeZone);
		long hours = ChronoUnit.HOURS.between(timeNow, timeStartOfScheduleToBeCancel);

		
		
		if(role.equals(Role.CUSTOMER) || role.equals(Role.ADMIN)) {
			if(userIdRequestCancel != scheduleToBeCancelled.getCustomerId()) 
				return ResponseEntity.badRequest().body("You are not allow to cancel task of this schedule !");
			if (scheduleToBeCancelled.getStatus().equals(ScheduleStatus.CANCEL) && taskRepo
					.findByScheduleIdAndTaskStatus(scheduleId, TaskStatus.CANCELLED_CAUSE_NOT_FOUND_STAFF) != null)
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
						"This task has been cancelled because not found any staff for your schedule. Let create another schedule if you want to continue");
			if(userIdRequestCancel == scheduleToBeCancelled.getCustomerId() && scheduleToBeCancelled.getStatus().equals(ScheduleStatus.CANCEL))
				return ResponseEntity.ok().body("You have already cancel this schedule !");
			if(customer.isBanned())
				return ResponseEntity.ok().body("You are banned - Cannot cancel this task anymore !");
			taskToBeCancelled = taskBuildupServ.cancelTaskByRole(Role.CUSTOMER, scheduleToBeCancelled, "The customer has cancelled the task !");
			if (hours < DURATION_TIMES_CUSTOMER_SHOULD_NOT_CANCEL_TASK) {
				if (taskToBeCancelled.getStaff() != null) {
					int subtract = customer.getProfiencyScore() - MINUS_POINTS_FOR_CUSTOMER_CANCEL_TASK;
					customer.setProfiencyScore(subtract < 0 ? 0 : subtract);
					if(customer.getProfiencyScore() == 0)
						customer.setBanned(true);
						taskBuildupServ.createAndSendNotification("You are banned for cancel task of this schedule for many times", "BANNED CUSTOMER", List.of(customer.getCustomerId()));
				}
			}
		}
		if(role.equals(Role.STAFF)) {	
			if(userIdRequestCancel != scheduleToBeCancelled.getStaffId()) 
				return ResponseEntity.badRequest().body("You are not allow to cancel task of this schedule !");
			taskToBeCancelled = taskBuildupServ.cancelTaskByRole(Role.STAFF, scheduleToBeCancelled, "The staff has cancelled the task !");
			if (hours < DURATION_TIMES_STAFF_SHOULD_NOT_CANCEL_TASK) {
				Staff staff = staffRepo.findById(userCancel.getUserId()).get();
				int subtract = staff.getProfiencyScore() - MINUS_POINTS_FOR_STAFF_CANCEL_TASK;
				staff.setProfiencyScore(subtract < 0 ? 0 : subtract);
				if(staff.getProfiencyScore() == 0)
					staff.setBanned(true);
				taskBuildupServ.createAndSendNotification("You are banned for cancel task of this schedule for many times", "BANNED CUSTOMER", List.of(staff.getStaffId()));

			}
		}
		if (taskToBeCancelled == null)
			return ResponseEntity.badRequest().body("Task not found to cancel");
		return ResponseEntity.ok().body("The task of this schedule has been cancelled successfully !");
	}
	
	// UPDATE TASK TO CHANGE THE TIME
	public ResponseEntity<?> updateTaskTimeWorking(HttpServletRequest request, Schedule oldSchedule, Schedule scheduleNewTimeWorking) {
		Schedule originalSchulde = scheduleRepo.findById(scheduleNewTimeWorking.getScheduleId()).orElse(null);
		int customerIdRequestUpdate = authorizationUtil.getUserIdFromAuthorizationHeader(request);

		if(originalSchulde == null)
			return ResponseEntity.badRequest().body("Old schedule not exists to update");
		
		if(!(customerIdRequestUpdate == oldSchedule.getCustomerId()))
			return ResponseEntity.badRequest().body("You are not allow to update this schedule");
		
		if(oldSchedule.getScheduleId() != scheduleNewTimeWorking.getScheduleId())
			return ResponseEntity.badRequest().body("New schedule should have the same ID of OldSchedule");
	
		
		TaskRes taskRes = taskBuildupServ.updateTaskOnScheduleChangeTime(scheduleNewTimeWorking);
		if (taskRes.getMessType().equals(TaskMessType.REJECT_UPDATE_TASK)) {
			return ResponseEntity.badRequest().body(taskRes.getMessage());
		}
				
		Map<String, Task> tasksOldAndNew = (Map<String, Task>) taskRes.getObject();
		Task newTask = tasksOldAndNew.get("newTask");
		if (newTask != null) {
			//TODO: SEND ASYNC NOTIFICATION EVENT - RECEIVER: ALL STAFF FOR NEW TASK OF NEW SCHEDULE
		}
		return ResponseEntity.ok().body(taskRes);
	}
	
	// APPROVE TASK
	public ResponseEntity<?> approveStaff(HttpServletRequest request, int taskId) {
		String role = authorizationUtil.getRoleFromAuthorizationHeader(request);
		if(!role.equals(Role.STAFF.name()))
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied - Only staff can apply");
		
		int staffId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
		Staff staff = staffRepo.findById(staffId).orElse(null);
		if(staff == null)
			return ResponseEntity.badRequest().body("Staff not exists to be approved");
		if(staff.isBanned())
			return ResponseEntity.badRequest().body("You are not allow to apply any task cause you are banned !");
		
		Task task = taskRepo.findById(taskId).orElse(null);
		if (task == null)
			return ResponseEntity.badRequest().body("Task not exists to be applied");
		if(task.getTaskStatus().name().contains("CANCELLED"))
			return ResponseEntity.badRequest().body("Cannot apply for this task - the task has been cancelled !");
		if(scheduleRepo.findByStaffIdAndStartDate(staff.getStaffId(), task.getSchedule().getStartDate()) != null ) 
			return ResponseEntity.badRequest().body("You cannot apply for this task cause you have duplicated time start to work!"); 
		if(task.getStaffId() != null && task.getStaffId() != staffId)
			return ResponseEntity.badRequest().body("Sorry, The task has already applied by another staff !");
		
		if(task.getStaffId() != null && task.getStaffId() == staffId)
			return ResponseEntity.ok().body("You has applied this task successfully !");
		
		TaskRes taskRes = taskBuildupServ.approveQualifiedStaff(staff, task);
		if (taskRes.getMessType().name().contains("REJECT"))
			return ResponseEntity.badRequest().body(taskRes.getMessage());

		Task approvedTask = (Task) taskRes.getObject();
		TaskViewDTO approvedTaskView = taskBuildupServ.convertIntoTaskViewDtoFromTask(approvedTask);
		return ResponseEntity.ok().body(approvedTaskView);
	}

	// REPORT TASK
	public ResponseEntity<?> reportTaskByStaff(HttpServletRequest request, int taskId, TaskReportType taskReportType, TaskReportNewDTO reportnewDTO) {
		int userReport = authorizationUtil.getUserIdFromAuthorizationHeader(request);
		Role userReportRole = Role.valueOf(authorizationUtil.getRoleFromAuthorizationHeader(request));
		Task taskToBeReported = taskRepo.findById(taskId).orElse(null);

		if (taskToBeReported == null)
			return ResponseEntity.badRequest().body("Task not exist to report !");

		if (!(taskToBeReported.getStaffId() != null 
				&& !taskToBeReported.getTaskStatus().name().contains("CANCELLED")
				&& userReportRole.equals(Role.STAFF) 
				&& userReport == taskToBeReported.getStaffId()))
			return ResponseEntity.badRequest().body("You are not allow to report task progress for this task");

		TaskRes<TaskReport> taskReportedRes = taskBuildupServ.reportTask(taskToBeReported, taskReportType, reportnewDTO);
		if (taskReportedRes == null)
			return ResponseEntity.badRequest().body("Task reported failed");
		if (taskReportedRes.getMessType().name().contains("REJECT"))
			return ResponseEntity.badRequest().body(taskReportedRes.getMessage());



		return ResponseEntity.ok().body(taskReportedRes.getObject());
	}
	
	public ResponseEntity<?> getTaskReportListByTask(int taskId){
		List<TaskReport> taskReport = taskReportRepo.findAllByTaskId(taskId);
		
		Task task = taskRepo.findById(taskId).orElse(null);
		if(task == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not exist ");
		if(task.getTaskStatus().name().contains("CANCELLED"))
			return ResponseEntity.status(HttpStatus.OK).body("The task has been cancelled. No any report for this task !");
		if (taskReport.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Have not any report for this task !");
		
		List<TaskReport> taskReports = taskReportRepo.findAllByTaskId(task.getTaskId());

		if (!taskReport.isEmpty()) {
			taskReports.forEach(x -> {
				List<Image> reportTaskImgs = imgRepo
						.findAllByEntityIdAndImageType(x.getTaskReportId(), ImageType.WORKING)
						.orElse(List.of());
				x.setTaskReportImages(reportTaskImgs);
			});
		}
		return ResponseEntity.ok().body(taskReport);
	}
	
	public ResponseEntity<?> getAllStaff(){
		List<Staff> staffs = staffRepo.findAll();
		if(staffs.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found any staff exists");
		return ResponseEntity.ok().body(staffs);
	}
	
	//TODO: SHUT DOWN AUTO CREATE TASK AUTO MATICALLY


}
