package housemate.services;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import housemate.constants.Enum.TaskReportType;
import housemate.constants.Enum.TaskStatus;
import housemate.constants.ImageType;
import housemate.constants.Role;
import housemate.constants.ScheduleStatus;
import static housemate.constants.ServiceTaskConfig.*;
import housemate.entities.Image;
import housemate.entities.Order;
import housemate.entities.Schedule;
import housemate.entities.Service;
import housemate.entities.ServiceType;
import housemate.entities.Staff;
import housemate.entities.Task;
import housemate.entities.TaskReport;
import housemate.entities.UserAccount;
import housemate.entities.UserUsage;
import housemate.models.TaskReportNewDTO;
import housemate.models.TaskViewDTO;
import housemate.models.TaskViewDTO.CustomerViewOnTask;
import housemate.models.TaskViewDTO.ServiceViewOnTask;
import housemate.repositories.ImageRepository;
import housemate.repositories.OrderItemRepository;
import housemate.repositories.OrderRepository;
import housemate.repositories.ScheduleRepository;
import housemate.repositories.ServiceRepository;
import housemate.repositories.ServiceTypeRepository;
import housemate.repositories.StaffRepository;
import housemate.repositories.TaskReportRepository;
import housemate.repositories.TaskReposiotory;
import housemate.repositories.UserRepository;
import housemate.repositories.UserUsageRepository;
import housemate.responses.TaskRes;
import housemate.responses.TaskRes.TaskMessType;
import jakarta.transaction.Transactional;

@Component
public class TaskBuildupService {

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
	
	@Autowired
	OrderRepository orderRepo;
	
	@Autowired
	ServiceRepository servRepo;
	
	@Autowired
	ServiceTypeRepository servTypeRepo;
	
	@Autowired
	TaskReportRepository taskReportRepo;
	
	@Autowired
	UserUsageRepository userUsageRepo;
	
	@Autowired
	OrderItemRepository orderItemRepo;
	
	ModelMapper mapper = new ModelMapper();
	
	@Autowired
	TaskScheduler taskScheduler;

	private final ZoneId dateTimeZone = ZoneId.of("Asia/Ho_Chi_Minh");
	
	private static final Logger log = LoggerFactory.getLogger(TaskBuildupService.class);

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	
	private static final Map<Integer, ScheduledFuture<?>> eventNotiList = new HashMap<>();
	

	
	//======CREATE TASK======
	@Scheduled(cron = "0 0 0 * * *", zone = "Asia/Ho_Chi_Minh") // call this at every 24:00 PM
	public List<Task> createTasksOnUpcomingSchedulesAutoByFixedRate() {
		List<Task> taskList = new ArrayList<>();
		try {
			// get all schedule and filter which schedule will coming up in tomorrow
			List<Schedule> schedules = scheduleRepo.findAllScheduleInUpComing(ScheduleStatus.PROCESSING, 1);
			// generate task for these schedule
			if (schedules.isEmpty())
				return List.of();
			for (Schedule schedule : schedules)
				taskList.add(this.createTask(schedule));
			TaskBuildupService.createAndSendNotification("NEW TASK COMING !", "UPCOMING TASK",
					List.of(staffRepo.findAll().stream().map(x -> x.getStaffId())));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return taskList;
	}
	
	public List<Task> createTaskOnUpComingSchedule(Schedule newSchedule) {
		List<Schedule> schedules = scheduleRepo.findAllByParentScheduleAndInUpComing(ScheduleStatus.PROCESSING, 1, newSchedule.getScheduleId());
		List<Task> taskList = new ArrayList<>();
		if (schedules.isEmpty()) 
				return List.of();
		for (Schedule theSchedule : schedules) {
			taskList.add(this.createTask(theSchedule));
		}
		TaskBuildupService.createAndSendNotification("NEW TASK COMING !", "UPCOMING TASK",
				List.of(staffRepo.findAll().stream().map(x -> x.getStaffId())));
		return taskList;
	}

	@Transactional
	public Task createTask(Schedule schedule) {
		// Check if the task for this schedule have created before by system
		Task task = taskRepo.findExistingTaskForSchedule(schedule.getScheduleId());
		log.info("SCHEDULE ID {} - IS TASK HAS EXISTED == CREATE {}", schedule.getScheduleId(), task);
		Task savedTask = null;
		try {
			if (task == null) {
				task = new Task();
				task.setScheduleId(schedule.getScheduleId());
				task.setCreatedAt(LocalDateTime.now(dateTimeZone));
				task.setTaskStatus(TaskStatus.PENDING_APPLICATION);
				task.setStaffId(null);
				task.setReceivedAt(null);
				savedTask = taskRepo.save(task);
				Schedule scheduleToUpdate = scheduleRepo.findById(schedule.getScheduleId()).get();
				scheduleToUpdate.setStatus(ScheduleStatus.PROCESSING);
				scheduleToUpdate.setOnTask(true);
				scheduleRepo.save(scheduleToUpdate);
				//TODO: CREATE EVENT
				this.createEventSendNotiWhenTimeComing(task, schedule.getStartDate());
				this.createEventSendNotiUpcomingTask(task, schedule.getStartDate(),5);
				TaskBuildupService.createAndSendNotification(
						"Your schedule will be starting soon !",
						"UPCOMING SCHEDULE", List.of(schedule.getCustomerId()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
		
		return savedTask;
	}
	
	//======VIEW TASK IN DETAILS======
	public TaskViewDTO convertIntoTaskViewDtoFromTask(Task task) {
		TaskViewDTO taskView = new TaskViewDTO();
		try {			
			List<TaskReport> taskReports = taskReportRepo.findAllByTaskId(task.getTaskId());
			if (taskReports != null) {
				taskReports.forEach(x -> {
					List<Image> reportTaskImgs = imgRepo
							.findAllByEntityIdAndImageType(x.getTaskReportId(), ImageType.WORKING)
							.orElse(List.of());
					x.setTaskReportImages(reportTaskImgs);
				});
			}
			
			Schedule schedule = scheduleRepo.findById(task.getScheduleId()).orElse(null);
			UserAccount customerInfoFrAcc = userRepo.findById(schedule.getCustomerId()).orElse(null);
			List<Image> customerAvatar = imgRepo
					.findAllByEntityIdAndImageType(customerInfoFrAcc.getUserId(), ImageType.AVATAR)
					.orElse(List.of());
			CustomerViewOnTask customerViewOnTask = mapper.map(customerInfoFrAcc, CustomerViewOnTask.class);
			customerViewOnTask.setAvatar(customerAvatar);
			
			//TODO: Consider Delete
			Order order = orderRepo.findById(orderItemRepo
					.findById(userUsageRepo.findById(schedule.getUserUsageId()).get().getOrderItemId()).getOrderId())
					.orElse(null);
			String addressWorking = order == null ? "No address exist" : order.getAddress();
			//String addressWorking =  orderRepo.findById(schedule.getUserUsageId()).get().getAddress();
			Service serviceInfoFrServ = servRepo.findByServiceId(schedule.getServiceId()).orElse(null);
			ServiceType serviceType = servTypeRepo.findById(schedule.getServiceTypeId()).orElse(null);
			ServiceViewOnTask service = mapper.map(serviceInfoFrServ, ServiceViewOnTask.class);
			service.setServiceType(serviceType);

			taskView = mapper.map(task, TaskViewDTO.class);
			taskView.setCustomer(customerViewOnTask);
			taskView.setAddressWorking(addressWorking);
			taskView.setService(service);
			taskView.setTaskReportList(taskReports);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return taskView;
	}
	
	//======CANCEL TASK======	
	public Task cancelTaskByRole(Role role, Schedule scheduleHasTaskToBeCancelled, String cancelReason) {
		Task taskToBeCancelled = taskRepo.findExistingTaskForSchedule(scheduleHasTaskToBeCancelled.getScheduleId());
		log.info("IS TASK HAS EXISTED == {}" + taskToBeCancelled);
		if (taskToBeCancelled != null) {
			switch (role) {
			case CUSTOMER, ADMIN:
				taskToBeCancelled = this.cancelTaskByCustomer(scheduleHasTaskToBeCancelled, taskToBeCancelled, Optional.of(cancelReason));
				break;
			case STAFF:
				taskToBeCancelled = this.cancelTaskByStaff(scheduleHasTaskToBeCancelled, taskToBeCancelled, Optional.of(cancelReason));
				break;
			default:
				throw new IllegalArgumentException("Unexpected value: " + role);
			}
		}
		return taskToBeCancelled;
	}
	
	@Transactional
	public Task cancelTaskByCustomer(Schedule scheduleHasTaskToBeCancelledByCustomer, Task taskToBeCancelled, Optional<String> cancelReason) {
		String taskNoteMess = cancelReason.orElse("The customer has cancelled the task !");
		taskToBeCancelled.setTaskStatus(TaskStatus.CANCELLED_BY_CUSTOMER);
		taskToBeCancelled.setTaskNote(taskNoteMess);
		scheduleHasTaskToBeCancelledByCustomer.setStatus(ScheduleStatus.CANCEL);
		// SHUTDOWN EVENT COUNT DOWN OF OLD TASK OF OLD SCHEDULE
		if (eventNotiList.get(taskToBeCancelled.getTaskId()) != null) {
			eventNotiList.get(taskToBeCancelled.getTaskId()).cancel(true);
			eventNotiList.remove(taskToBeCancelled.getTaskId());
		}
		if (taskToBeCancelled.getStaffId() != null)
			TaskBuildupService.createAndSendNotification(
					"Sorry, Thu customer has cancelled your task which you have applied !",
					"TASK CANCELLED BY CUSTOMER", List.of(taskToBeCancelled.getStaffId()));

		log.info("CANCEL BY CUSTOMER - EVENT OF TASK {} IS EXIST : {}", taskToBeCancelled.getTaskId(),
				eventNotiList.get(taskToBeCancelled.getTaskId()));

		return taskToBeCancelled;
	}

	@Transactional
	public Task cancelTaskByStaff(Schedule scheduleHasTaskToBeCancelledByStaff, Task taskToBeCancelled, Optional<String> cancelReason) {
		Task renewTaskFormApplication = null;
		String taskNoteMess = cancelReason.orElse("The staff has cancelled the task !");
		taskToBeCancelled.setTaskStatus(TaskStatus.CANCELLED_BY_STAFF);
		taskToBeCancelled.setTaskNote(taskNoteMess);
		scheduleHasTaskToBeCancelledByStaff.setStaffId(0);
		taskRepo.save(taskToBeCancelled);
		renewTaskFormApplication = this.createTask(scheduleHasTaskToBeCancelledByStaff);
		// SHUTDOWN EVENT COUNT DOWN OF OLD TASK OF OLD SCHEDULE
		if (eventNotiList.get(taskToBeCancelled.getTaskId()) != null) {
			eventNotiList.get(taskToBeCancelled.getTaskId()).cancel(true);
			eventNotiList.remove(taskToBeCancelled.getTaskId());
		}
		log.info("CANCEL BY STAFF - EVENT OF TASK {} IS EXIST : {}", taskToBeCancelled.getTaskId(),
				eventNotiList.get(taskToBeCancelled.getTaskId()));
		
		// TODO: SEND NOTI TO CUSTOMER WHO HAS THE TASK CANCELLED BY STAFF
		TaskBuildupService.createAndSendNotification(
				"The old staff has rejected to do your schedule ! Please waiting for other staff apply on your schedule",
				"TASK CANCELLED BY STAFF", List.of(scheduleHasTaskToBeCancelledByStaff.getCustomerId()));

		return renewTaskFormApplication;
	}
	
	//======UPDATE TASK TIME WORKING======
	@Transactional
	public TaskRes<Map<String, Task>> updateTaskOnScheduleChangeTime(Schedule scheduleNewTime) {
		TaskRes taskRes = null;
		LocalDateTime timeNow = LocalDateTime.now(dateTimeZone);
		LocalDateTime newTimeScheduleStart = scheduleNewTime.getStartDate();
		long hoursDiff = ChronoUnit.HOURS.between(timeNow, newTimeScheduleStart);
		long dayDiff = ChronoUnit.DAYS.between(timeNow, newTimeScheduleStart);
		Map<String, Task> tasksOldAndNew = new HashMap<>();

		Task taskToBeChangedTime = taskRepo.findExistingTaskForSchedule(scheduleNewTime.getScheduleId());
		if (taskToBeChangedTime == null)
			return TaskRes.build(tasksOldAndNew, TaskMessType.REJECT_UPDATE_TASK, "Not found task to update");

		// Change time working of task in inverval current day

		if (hoursDiff <= 3 && hoursDiff > 0) {
			return TaskRes.build(tasksOldAndNew, TaskMessType.REJECT_UPDATE_TASK,
					"Setting the new schedule for task with the time start from " + timeNow.plusHours(3)
							+ "to allow the system has enough time for finding staff !");
		}
		Task oldTask = null;
		Task newTask = null;
		try {
			oldTask = cancelTaskByRole(Role.CUSTOMER, scheduleNewTime, "The customer has changed the time !");
			if (oldTask == null) {
				throw new NullPointerException("Null cancelled task of old schedule");
			}
			tasksOldAndNew.put("oldTask", oldTask);
			if (dayDiff >= 0 && dayDiff <= 1) { // if the daydiff > 1 do not care system care
				newTask = this.createTask(scheduleNewTime);
				if (newTask == null) {
					throw new NullPointerException("Null updated task of new schedule");
				}
				scheduleRepo.save(scheduleNewTime); // this will auto update time for oldschedle based on the same id
				tasksOldAndNew.put("newTask", newTask);
			}
			// TODO: SEND NOTI TO STAFF OF OLD SCHEDULE
			if (oldTask.getStaffId() != null) {
				TaskBuildupService.createAndSendNotification(
						"The task has been cancelled because the customer has changed time working !",
						"TASK CANCELLED BY CUSTOMER", List.of(oldTask.getStaffId()));
			}
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return TaskRes.build(tasksOldAndNew, TaskMessType.REJECT_UPDATE_TASK,
					"Something Errors. Update time working for task failed !");
		}
		return TaskRes.build(tasksOldAndNew, TaskMessType.OK, "Updated task successfully !");
	}
	
	
	// ======APPROVE STAFF======
	@Transactional
	public TaskRes<Task> approveQualifiedStaff(Staff staff, Task task) {
		LocalDateTime timeNow = LocalDateTime.now(dateTimeZone);
		LocalDateTime timeStartWorking = task.getSchedule().getStartDate();
		long hoursDiff = ChronoUnit.HOURS.between(timeNow, timeStartWorking);
		TaskRes<Task> taskRes = null;

		if (!task.getTaskStatus().equals(TaskStatus.PENDING_APPLICATION))
			taskRes = TaskRes.build(task, TaskMessType.REJECT_APPROVE_STAFF,
					"Waiting the task to open for applications !");

		if (staff.getProfiencyScore() < BAD_STAFF_PROFICIENT_SCORE && hoursDiff > DURATION_TIMES_ALLOW_BAD_STAFF_PROFICENT_SCORE_APPLY) 
			taskRes = TaskRes.build(task, TaskMessType.REJECT_APPROVE_STAFF,
					"Unsufficent profiency score. Comback to apply if the task not found any staff before the time schedule starts " + DURATION_TIMES_ALLOW_BAD_STAFF_PROFICENT_SCORE_APPLY + "hours !");
		
		if (staff.getProfiencyScore() >= 30 || (staff.getProfiencyScore() < 30 && hoursDiff <= 3 && hoursDiff > 0)) {
			try {
				task.setStaffId(staff.getStaffId());
				task.setReceivedAt(LocalDateTime.now(dateTimeZone));
				task.setStaff(staff);
				task.setTaskStatus(TaskStatus.PENDING_WORKING);
				task.getSchedule().setStatus(ScheduleStatus.PENDING);
				task.getSchedule().setStaffId(staff.getStaffId());
				taskRes = TaskRes.build(task, TaskMessType.OK, "Approved staff Successfully !");
				TaskBuildupService.createAndSendNotification(
						"We have found staff will work on your schedule. Let contact with our staff !", "FOUND STAFF",
						List.of(task.getSchedule().getCustomerId()));
			} catch (Exception e) {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return TaskRes.build(task, TaskMessType.REJECT_UPDATE_TASK, "Something Errors. Approved failed !");
			}
		}
		return taskRes;
	}
	
	@Transactional
	public TaskRes<TaskReport> reportTask(Task task, TaskReportType taskReport, TaskReportNewDTO reportNewDTO) {
		TaskReport taskReportResult = new TaskReport();
		Service serviceInUsed = servRepo.findByServiceId(task.getSchedule().getServiceId()).orElse(null);
		UserUsage userUsage = userUsageRepo.findById(task.getSchedule().getUserUsageId()).get();
		if (serviceInUsed == null) {
			return TaskRes.build(taskReportResult, TaskMessType.REJECT_REPORT_TASK,
					"The kind of service not exist to report task of this service");
		}
		TaskReport checkReportExists = taskReportRepo.findByTaskIdAndTaskStatus(task.getTaskId(), TaskStatus.valueOf(taskReport.name()));
		if (checkReportExists != null) {
			checkReportExists.setNote(reportNewDTO.getNote());
			return TaskRes.build(checkReportExists, TaskMessType.OK,
					"Update report successfully");
		}
		try {
			taskReportResult.setTaskId(task.getTaskId());
			taskReportResult.setReportAt(LocalDateTime.now());
			taskReportResult.setNote(reportNewDTO.getNote());
			switch (taskReport) {
			case ARRIVED: {
				//Checkconstraint
				long minutesDiff = ChronoUnit.MINUTES.between(LocalDateTime.now(dateTimeZone), task.getSchedule().getStartDate());
				if (minutesDiff > DURATION_MINUTES_TIMES_STAFF_START_REPORT)
					return TaskRes.build(checkReportExists, TaskMessType.REJECT_REPORT_TASK,
							"Please wait for the task report open to start report task progression. Task report wiil open at " + task.getSchedule().getStartDate().minusMinutes(DURATION_MINUTES_TIMES_STAFF_START_REPORT));
				// Set up
				task.setTaskStatus(TaskStatus.ARRIVED);
				task.getSchedule().setStatus(ScheduleStatus.INCOMING);
				taskReportResult.setTaskStatus(task.getTaskStatus());
				// Send notification
				TaskBuildupService.createAndSendNotification(
						"Your task is being processed by our staff. Please wait for our staff done their job !",
						"ARRIVED TASK PROGRESSION", List.of(task.getSchedule().getCustomerId()));
				break;
			}
			case DOING: {
				// Check if status arrvived is passed through ?
				TaskReport checkArrivedReportExists = taskReportRepo.findByTaskIdAndTaskStatus(task.getTaskId(),
						TaskStatus.ARRIVED);
				if (checkArrivedReportExists == null)
					return TaskRes.build(taskReportResult, TaskMessType.REJECT_REPORT_TASK,
							"Please report for the status ARRIVED first");
				// Check for return service type
				boolean isReturnService = serviceInUsed.getGroupType().equals("RETURN_SERVICE");
				if (isReturnService) {
					Integer quantity = reportNewDTO.getQtyOfGroupReturn();
					if (quantity == null)
						return TaskRes.build(taskReportResult, TaskMessType.REJECT_REPORT_TASK,
								"Please fill the quantity for the service in type \"Return service\"");
					if (!(serviceInUsed.getMin() == 0 && serviceInUsed.getMax() == 0))
						if (!(quantity >= serviceInUsed.getMin() && quantity <= serviceInUsed.getMax()))
							return TaskRes.build(taskReportResult, TaskMessType.REJECT_REPORT_TASK,
									"Please fill the quantity for the service in range [" + serviceInUsed.getMin()
											+ " - " + serviceInUsed.getMax() + "]");
					if (!(quantity <= userUsage.getRemaining() && quantity > 0))
						return TaskRes.build(taskReportResult, TaskMessType.REJECT_REPORT_TASK,
								"Oops, You only have the quantity of " + userUsage.getRemaining() + ". Please fill the quantity for the service in range [" + 1 + " - "
										+ userUsage.getRemaining() + "]");
					task.getSchedule().setQuantityRetrieve(quantity);
				}
				// Set up
				task.setTaskStatus(TaskStatus.DOING);
				task.getSchedule().setStatus(ScheduleStatus.INCOMING);
				taskReportResult.setTaskStatus(task.getTaskStatus());
				// Send notification
				TaskBuildupService.createAndSendNotification(
						"Your task is being processed by our staff. Please wait for our staff done their job !",
						"DOING TASK PROGRESSION", List.of(task.getSchedule().getCustomerId()));
				break;
			}
			case DONE: {
				TaskReport checkArrivedReportExists = taskReportRepo.findByTaskIdAndTaskStatus(task.getTaskId(),
						TaskStatus.ARRIVED);
				if (checkArrivedReportExists == null)
					return TaskRes.build(taskReportResult, TaskMessType.REJECT_REPORT_TASK,
							"Please report task progress for the status ARRIVED ");
				TaskReport checkDoingReportExists = taskReportRepo.findByTaskIdAndTaskStatus(task.getTaskId(),
						TaskStatus.DOING);
				if (checkDoingReportExists == null)
					return TaskRes.build(taskReportResult, TaskMessType.REJECT_REPORT_TASK,
							"Please report task progress the status DOING ");
				if (LocalDateTime.now().isBefore(task.getSchedule().getEndDate()))
					return TaskRes.build(checkReportExists, TaskMessType.REJECT_REPORT_TASK,
							"You only allow report task progression DONE after at the time the the task end working require: " + task.getSchedule().getEndDate());
			
				// Set up
				task.setTaskStatus(TaskStatus.DONE);
				task.getSchedule().setStatus(ScheduleStatus.DONE);
				taskReportResult.setTaskStatus(task.getTaskStatus());
				int newQuantityRemaining = userUsage.getRemaining() - task.getSchedule().getQuantityRetrieve();
				userUsage.setRemaining(newQuantityRemaining);

				// Send notification
				TaskBuildupService.createAndSendNotification(
						"Your task has been done by our staff. Let enjoy the result !", "DONE TASK PROGRESSION",
						List.of(task.getSchedule().getCustomerId()));
				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + taskReport);
			}
			taskRepo.save(task);
			TaskReport reportedTask = taskReportRepo.save(taskReportResult);
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			e.printStackTrace();
			return TaskRes.build(taskReportResult, TaskMessType.REJECT_REPORT_TASK,
					"Something Errors. Report failed !");
		}
		return TaskRes.build(taskReportResult, TaskMessType.OK, "Report task successfully !");
	}
	
	// === SETTING TIME FOR AUTOMATIC NOTIFICATION===//
	public static void createAndSendNotification(String mess, String title, List<?> receivers) {
		// TODO: BUILD MESSAGE
		// TODO SEND NOTIFICATION
	}

	public void createEventSendNotiWhenTimeComing(Task task, LocalDateTime timeStartTask) {
		ZonedDateTime timeStartTaskZone = timeStartTask.atZone(dateTimeZone);
		Instant timeSendNotiInstant = timeStartTaskZone.toInstant();

		Runnable runnableTask = new Runnable() {
			@Override
			public void run() {
				if (task.getStaffId() == null) {
					task.setTaskStatus(TaskStatus.CANCELLED_CAUSE_NOT_FOUND_STAFF);
					Schedule schedule = scheduleRepo.findById(task.getScheduleId()).get();
					schedule.setStatus(ScheduleStatus.CANCEL);
					scheduleRepo.save(schedule);
					TaskBuildupService.createAndSendNotification(
							"Sorry, time is coming but staff is during peak hours, there is no staff to serve you at this time !",
							"NOT FOUND STAFF", List.of(task.getSchedule().getCustomerId()));
					log.info("TASK {} CLOSED AT {} STAFF IS NULL", task.getTaskId(), dateFormat.format(new Date()));
				}
				if (task.getStaffId() != null) {
					TaskBuildupService.createAndSendNotification("Let go to welcome your staff coming to your home ! !",
							"STAFF COMING", List.of(task.getSchedule().getCustomerId()));
					log.info("TASK {} CLOSED AT {} STAFF NOT NULL", task.getTaskId(), dateFormat.format(new Date()));

				}
			}
		};
		ScheduledFuture<?> taskEvent = taskScheduler.schedule(runnableTask, timeSendNotiInstant);
		eventNotiList.put(task.getTaskId(), taskEvent);
		taskScheduler.scheduleAtFixedRate(() -> {
		}, Instant.now(), Duration.ofSeconds(3));
		log.info("======createEventSendNotiUpcomingTask======");
	}

	public void createEventSendNotiUpcomingTask(Task task, LocalDateTime timeStartTask, int periodHourBefore) {
		ZonedDateTime timeStartTaskZone = timeStartTask.atZone(dateTimeZone).minusHours(periodHourBefore);
		Instant timeSendNotiInstant = timeStartTaskZone.toInstant();

		Runnable runnableTask = new Runnable() {

			@Override
			public void run() {
				if (task.getStaff() == null) {
					TaskBuildupService.createAndSendNotification(
							"We are trying to find the staff for your task, please waiting for staff apply !",
							"NOT FOUND STAFF", List.of(task.getSchedule().getCustomerId()));
					log.info("TASK {} UPCOMING NOTI SEND - STAFF IS NULL - SENT AT {}", task.getTaskId(), dateFormat.format(new Date()));
				}
				if (task.getStaff() != null) {
					task.getSchedule().setStatus(ScheduleStatus.INCOMING);
					taskRepo.save(task);			
					TaskBuildupService.createAndSendNotification(
							"Our staff will coming to your house, please wait for our staff coming !", "INCOMING ",
							List.of(task.getSchedule().getCustomerId()));
					TaskBuildupService.createAndSendNotification("You have the task today at "
							+ userRepo.findByUserId(task.getSchedule().getCustomerId()).getFullName() + "'s house !",
							"INCOMING ", List.of(task.getStaffId()));
					log.info("TASK {} UPCOMING NOTI SEND - STAFF NOT NULL - SENT AT {}", task.getTaskId(), dateFormat.format(new Date()));
				}

			}
		};
		ScheduledFuture<?> taskEvent = taskScheduler.schedule(runnableTask, timeSendNotiInstant);
		eventNotiList.put(task.getTaskId(), taskEvent);
		taskScheduler.scheduleAtFixedRate(() -> {
			log.info("TIMENOW EVENT 2 {}", dateFormat.format(new Date()));
		}, Instant.now(), Duration.ofSeconds(5));
		log.info("======createEventSendNotiUpcomingTask======");

	}

}
