package housemate.services;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
import static housemate.constants.ServiceConfiguration.*;
import housemate.constants.Enum.TaskMessType;
import housemate.constants.Enum.TaskReportType;
import housemate.constants.Enum.TaskStatus;
import housemate.constants.ImageType;
import housemate.constants.Role;
import housemate.constants.ScheduleStatus;
import housemate.entities.Image;
import housemate.entities.Order;
import housemate.entities.OrderItem;
import housemate.entities.Schedule;
import housemate.entities.Service;
import housemate.entities.ServiceFeedback;
import housemate.entities.ServiceType;
import housemate.entities.Staff;
import housemate.entities.Task;
import housemate.entities.TaskReport;
import housemate.entities.UserAccount;
import housemate.entities.UserUsage;
import housemate.models.TaskReportNewDTO;
import housemate.models.TaskViewDTO;
import housemate.models.TaskViewDTO.CustomerViewOnTask;
import housemate.models.TaskViewDTO.ServiceFeedbackViewOnTask;
import housemate.models.TaskViewDTO.ServiceViewOnTask;
import housemate.repositories.FeedbackRepository;
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
import jakarta.transaction.Transactional;

@Component
public class TaskBuildupService {

    @Autowired
    private ScheduleRepository scheduleRepo;

    @Autowired
    private TaskReposiotory taskRepo;

    @Autowired
    private StaffRepository staffRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ImageRepository imgRepo;

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private ServiceRepository servRepo;

    @Autowired
    private ServiceTypeRepository servTypeRepo;

    @Autowired
    private TaskReportRepository taskReportRepo;

    @Autowired
    private  UserUsageRepository userUsageRepo;

    @Autowired
    private OrderItemRepository orderItemRepo;

    @Autowired
    private FeedbackRepository feedbRepo;

    private  ModelMapper mapper = new ModelMapper();

    @Autowired
    private TaskScheduler taskScheduler;

    private final ZoneId dateTimeZone = ZoneId.of("Asia/Ho_Chi_Minh");

    private static final Logger log = LoggerFactory.getLogger(TaskBuildupService.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    private static final Map<Integer, List<ScheduledFuture<?>>> eventNotiList = new HashMap<>();
       
    private static final String notiTitleForTaskStatus = "Trạng thái công việc";
   
    
    //MARKUP ======CREATE TASK======
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Ho_Chi_Minh") // call this at every 24:00 PM
    public List<Task> createTasksOnUpcomingSchedulesAutoByFixedRate() {
	List<Task> taskList = new ArrayList<>();
	try {
	    // get all schedule and filter which schedule will coming up in tomorrow
	    List<Schedule> schedules = scheduleRepo.findAllScheduleInUpComing(ScheduleStatus.PROCESSING, 1);
	    // generate task for these schedule
	    if (schedules.isEmpty())
		return List.of();
	    for (Schedule schedule : schedules) {
		taskList.add(this.createTask(schedule));		 
	    }

	    if (!taskList.isEmpty()) {
		// TODO: NOTI TO CUSTOMER FOR TASK
		taskList.stream().forEach(x -> {
		    TaskBuildupService.createAndSendNotification(
			    x, // Task
			    "Chờ tìm nhân viên",
			    "Đang tìm kiếm nhân viên", // Mess
			    String.valueOf(x.getSchedule().getCustomerId())); // Receiver
		});
		// TODO: NOTI NEW TASK LIST FOR STAFF
		TaskBuildupService.createAndSendNotification(
			null,
			"Việc mới",
			"Việc mới ngày " + LocalDate.now().getDayOfMonth(),
			Role.STAFF.name());
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    return List.of();
	}
	return taskList;
    }
    
    
    public List<Task> createTaskOnUpComingSchedule(Schedule newSchedule) {
	List<Schedule> schedules = scheduleRepo.findAllByParentScheduleAndInUpComing(ScheduleStatus.PROCESSING, 1, newSchedule.getParentScheduleId());	
	log.info("IS SCHEDULES EMPTY {} ", schedules.size());
	List<Task> taskList = new ArrayList<>();
	try {
		log.debug("SCHEDULES WHEN FIND ALL BY PARENTS - IS EMPTY {} | IS NULL: {}", schedules.isEmpty(), schedules == null);
	    if (schedules.isEmpty())
		return List.of();
	    for (Schedule theSchedule : schedules) 
		taskList.add(this.createTask(theSchedule));
	    
	    if (!taskList.isEmpty()) {
		// TODO: NOTI NEW TASK LIST FOR STAFF
		TaskBuildupService.createAndSendNotification(
			null,
			"Việc mới",
			"Việc mới ngày " + LocalDate.now().getDayOfMonth(),
			Role.STAFF.name());
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    return List.of();
	}
	return taskList;
    }

    @Transactional
    public Task createTask(Schedule schedule) {
	// Check if the task for this schedule have created before by system
	Task task = taskRepo.findExistingTaskForSchedule(schedule.getScheduleId());
	log.info("SCHEDULE ID {} - IS TASK OF THIS SCHEDULE HAS EXISTED BEFORE : {}", schedule.getScheduleId(), task);
	Task savedTask = null;
	try {
	    if (task == null) {
		task = new Task();
		task.setScheduleId(schedule.getScheduleId());
		task.setCreatedAt(LocalDateTime.now(dateTimeZone));
		task.setTaskStatus(TaskStatus.PENDING_APPLICATION);
		task.setStaffId(null);
		task.setReceivedAt(null);
		Schedule scheduleToUpdate = scheduleRepo.findById(schedule.getScheduleId()).get();
		scheduleToUpdate.setStatus(ScheduleStatus.PROCESSING);
		scheduleToUpdate.setOnTask(true);
		scheduleRepo.save(scheduleToUpdate);
		task.setSchedule(schedule);
		savedTask = taskRepo.save(task);
		
		//MARKUP CALL EVENTS WHEN CREATE TASK
		if (savedTask != null) {
		    this.createEventSendNotiWhenTimeComing(task, schedule.getStartDate());
		    this.createEventSendNotiUpcomingTask(task, schedule.getStartDate(), DURATION_HOURS_SEND_INCOMING_NOTI_BEFORE.getNum());
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
	}
	
	return savedTask;
    }

    //MARKUP ======VIEW TASK IN DETAILS======
    public TaskViewDTO convertIntoTaskViewDtoFromTask(Task task) {
	TaskViewDTO taskView = new TaskViewDTO();
	try {
	    List<TaskReport> taskReports = taskReportRepo.findAllByTaskId(task.getTaskId());
	    if (taskReports != null) {
		taskReports.forEach(x -> {
		    List<Image> reportTaskImgs = imgRepo
			    .findAllByEntityIdAndImageType(x.getTaskReportId(), ImageType.WORKING).orElse(List.of());
		    x.setTaskReportImages(reportTaskImgs);
		});
	    }
	    Schedule schedule = scheduleRepo.findById(task.getScheduleId()).orElse(null);
	    UserAccount customerInfoFrAcc = userRepo.findById(schedule.getCustomerId()).orElse(null);
	    List<Image> customerAvatar = imgRepo
		    .findAllByEntityIdAndImageType(customerInfoFrAcc.getUserId(), ImageType.AVATAR).orElse(List.of());
	    CustomerViewOnTask customerViewOnTask = mapper.map(customerInfoFrAcc, CustomerViewOnTask.class);
	    customerViewOnTask.setAvatar(customerAvatar);

	    String servicePackageName = "";
	    OrderItem orderItem = orderItemRepo
		    .findById(userUsageRepo.findById(schedule.getUserUsageId()).get().getOrderItemId());
	    Service parentService = servRepo.findByServiceId(orderItem.getServiceId()).get();
	    if (parentService.isPackage())
		servicePackageName = parentService.getTitleName();
	    Order order = orderRepo.findById(orderItem.getOrderId()).orElse(null);

	    String addressWorking = order.getAddress();
	    Service serviceInfoFrServ = servRepo.findByServiceId(schedule.getServiceId()).orElse(null);
	    ServiceType serviceType = servTypeRepo.findById(schedule.getServiceTypeId()).orElse(null);
	    ServiceViewOnTask service = mapper.map(serviceInfoFrServ, ServiceViewOnTask.class);
	    service.setServiceType(serviceType);
	    List<Image> serviceImage = imgRepo
		    .findAllByEntityIdAndImageType(serviceInfoFrServ.getServiceId(), ImageType.SERVICE)
		    .orElse(List.of());
	    service.setImages(serviceImage);
	    service.setPackageName(servicePackageName);
	    
	    ServiceFeedback feedbackFrEntity = feedbRepo.findByCustomerIdAndTaskIdAndServiceId(schedule.getCustomerId(),
		    task.getTaskId(), service.getServiceId());
	    ServiceFeedbackViewOnTask feedback = null;
	    if (feedbackFrEntity != null)
		feedback = mapper.map(feedbackFrEntity, ServiceFeedbackViewOnTask.class);

	    taskView = mapper.map(task, TaskViewDTO.class);
	    taskView.setCustomer(customerViewOnTask);
	    taskView.setAddressWorking(addressWorking);
	    taskView.setService(service);
	    taskView.setTaskReportList(taskReports);
	    taskView.setFeedback(feedback);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return taskView;
    }

    //MARKUP ======CANCEL TASK======
    public Task cancelTaskByRole(Role role, Schedule scheduleHasTaskToBeCancelled, String cancelReason) {
	Task taskToBeCancelled = taskRepo.findExistingTaskForSchedule(scheduleHasTaskToBeCancelled.getScheduleId());
	log.info("CANCEL THE TASK {}" + taskToBeCancelled);
	if (taskToBeCancelled != null) {
	    switch (role) {
	    case CUSTOMER, ADMIN:
		taskToBeCancelled = this.cancelTaskByCustomer(scheduleHasTaskToBeCancelled, taskToBeCancelled, Optional.of(cancelReason));
	    
	    //TODO: NOTI CANCEL TO STAFF	
	    if (taskToBeCancelled != null && taskToBeCancelled.getStaff() != null) {
		TaskBuildupService.createAndSendNotification(
			taskToBeCancelled,
			"Hủy lịch",
			"Khách hàng hủy lịch làm việc ngày " + dateFormat.format(
				Date.from(taskToBeCancelled.getSchedule().getStartDate().toInstant(null))),
			String.valueOf(taskToBeCancelled.getStaffId()));
	    };
	    break;
	    case STAFF:
		taskToBeCancelled = this.cancelTaskByStaff(scheduleHasTaskToBeCancelled, taskToBeCancelled, Optional.of(cancelReason));
		
		// TODO: NOTI CANCEL TO CUSTOMER
		if (taskToBeCancelled != null && taskToBeCancelled.getStaff() != null)
		    TaskBuildupService.createAndSendNotification(
			    taskToBeCancelled,
			    "Hủy công việc",
			    "Nhân viên hủy lịch làm việc, vui lòng chờ nhân viên khác",
			    String.valueOf(taskToBeCancelled.getSchedule().getCustomerId()));	
		break;
	    default:
		throw new IllegalArgumentException("Unexpected value: " + role);
	    }
	}
	return taskToBeCancelled;
    }

    @Transactional
    public Task cancelTaskByCustomer(Schedule scheduleHasTaskToBeCancelledByCustomer, Task taskToBeCancelled, Optional<String> cancelReason) {
	String taskNoteMess = cancelReason.orElse("Khách hàng đã hủy công việc này !");
	taskToBeCancelled.setTaskStatus(TaskStatus.CANCELLED_BY_CUSTOMER);
	taskToBeCancelled.setTaskNote(taskNoteMess);
	scheduleHasTaskToBeCancelledByCustomer.setStatus(ScheduleStatus.CANCEL);
	taskRepo.save(taskToBeCancelled);
	
	//MARKUP CALL EVENTS WHEN CANCEL TASK
	this.CancelAllEventsByTaskId(taskToBeCancelled.getTaskId());
	
	log.info("CALL CANCEL BY CUSTOMER - EVENT OF TASK {} EXISTs : {}", taskToBeCancelled.getTaskId(), eventNotiList.get(taskToBeCancelled.getTaskId()));

	return taskToBeCancelled;
    }

    @Transactional
    public Task cancelTaskByStaff(Schedule scheduleHasTaskToBeCancelledByStaff, Task taskToBeCancelled, Optional<String> cancelReason) {
	Task renewTaskFormApplication = null;
	String taskNoteMess = cancelReason.orElse("Nhân viên đã hủy task !");
	taskToBeCancelled.setTaskStatus(TaskStatus.CANCELLED_BY_STAFF);
	taskToBeCancelled.setTaskNote(taskNoteMess);
	scheduleHasTaskToBeCancelledByStaff.setStaffId(0);
	taskRepo.save(taskToBeCancelled);
	renewTaskFormApplication = this.createTask(scheduleHasTaskToBeCancelledByStaff);
	
	//MARKUP CALL EVENTS WHEN CANCEL TASK
	this.CancelAllEventsByTaskId(taskToBeCancelled.getTaskId());
	
	log.info("CALL CANCEL BY STAFF - EVENT OF TASK {} EXISTs : {}", taskToBeCancelled.getTaskId(), eventNotiList.get(taskToBeCancelled.getTaskId()));

	return renewTaskFormApplication;
    }

    //MARKUP ======UPDATE TASK TIME WORKING======
    @Transactional
    public TaskRes<Map<String, Task>> updateTaskOnScheduleChangeTime(Schedule scheduleNewTime) {
	Map<String, Task> tasksOldAndNew = new HashMap<>();
	LocalDateTime timeNow = LocalDateTime.now(dateTimeZone);
	LocalDateTime newTimeScheduleStart = scheduleNewTime.getStartDate();
	long dayDiff = ChronoUnit.DAYS.between(timeNow, newTimeScheduleStart);
	Task oldTask = null;
	Task newTask = null;

	Task taskToBeChangedTime = taskRepo.findExistingTaskForSchedule(scheduleNewTime.getScheduleId());
	if (taskToBeChangedTime == null)
	    return TaskRes.build(tasksOldAndNew, TaskMessType.REJECT_UPDATE_TASK, "Lịch này chưa được đăng lên thành công việc!");

	try {
	    oldTask = cancelTaskByRole(Role.CUSTOMER, scheduleNewTime, "Khách hàng đổi lịch làm việc !");
	    if (oldTask == null) {
		throw new NullPointerException("Hủy lịch cũ thất bại");
	    }
	    tasksOldAndNew.put("oldTask", oldTask);
	    if (dayDiff >= 0 && dayDiff <= 1) { // if the daydiff > 1 do not care system care
		newTask = this.createTask(scheduleNewTime);
		scheduleRepo.save(scheduleNewTime); // this will auto update time for oldschedle based on the same id
		tasksOldAndNew.put("newTask", newTask);
	    }
	
	    // TODO: NOTI CANCEL TASK FOR CHANGE TIME WORKING TO STAFF
	    if (oldTask.getStaffId() != null) {
		    TaskBuildupService.createAndSendNotification(
			    oldTask,
			    "Hủy công việc",
			    "Lich công việc bị hủy do khách hàng đổi giờ làm việc ",
			    String.valueOf(oldTask.getStaffId()));
	    }
	    
	} catch (Exception e) {
	    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
	    return TaskRes.build(tasksOldAndNew, TaskMessType.REJECT_UPDATE_TASK,
		    "Lỗi xảy ra. Cập nhật lịch mới cho task thất bài !");
	}
	return TaskRes.build(tasksOldAndNew, TaskMessType.OK, "Cập nhật thời gian cho task thành công !");
    }

    //MARKUP ======APPROVE STAFF======
    @Transactional
    public TaskRes<Task> approveQualifiedStaff(Staff staff, Task task) {
	TaskRes<Task> taskRes = null;
	    try {
		task.setStaffId(staff.getStaffId());
		task.setReceivedAt(LocalDateTime.now(dateTimeZone));
		task.setStaff(staff);
		task.setTaskStatus(TaskStatus.PENDING_WORKING);
		task.getSchedule().setStatus(ScheduleStatus.PENDING);
		task.getSchedule().setStaffId(staff.getStaffId());
		taskRepo.save(task);
		taskRes = TaskRes.build(task, TaskMessType.OK, "Ứng tuyển thành công !");
				
		//Change task status into incoming if the time apply in the range defined before
		long hoursfrMinutes = ChronoUnit.MINUTES.between(LocalDateTime.now(dateTimeZone), task.getSchedule().getStartDate());
		log.info("KHOẢNG THỜI GIAN GIỮA THỜI ĐIỂM HIỆN TẠI VÀ THỜI ĐIỂM LÀM VIỆC {}", hoursfrMinutes);
		if(hoursfrMinutes >= 0 && hoursfrMinutes <= DURATION_HOURS_SEND_INCOMING_NOTI_BEFORE.getNum()) {
		    task.setTaskStatus(TaskStatus.INCOMING);
		    task.getSchedule().setStatus(ScheduleStatus.INCOMING);
		}

		//MARKUP CALL EVENTS FOR REPORT TASK
		this.createEventForReportTask(task, task.getSchedule().getStartDate(), task.getSchedule().getEndDate());
				
	    } catch (Exception e) {
		e.printStackTrace();
		TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		return TaskRes.build(task, TaskMessType.REJECT_UPDATE_TASK, "Something Errors. Approved failed !");
	    }
	    return taskRes;
    }

    @Transactional
    public TaskRes<TaskReport> reportTask(Task task, TaskReportType taskReport, TaskReportNewDTO reportNewDTO) {
	TaskReport taskReportResult = new TaskReport();
	UserUsage userUsage = userUsageRepo.findById(task.getSchedule().getUserUsageId()).get();
	Service serviceInUsed = servRepo.findByServiceId(task.getSchedule().getServiceId()).orElse(null);
	if (serviceInUsed == null)
	    return TaskRes.build(taskReportResult, TaskMessType.REJECT_REPORT_TASK,
		    "Loại dịch vụ này không tồn tại ! Từ chối báo cáo cho công việc với loại dịch vụ không tồn tại !");
	//Check report exists before
	TaskReport checkReportExists = taskReportRepo.findByTaskIdAndTaskStatus(task.getTaskId(), TaskStatus.valueOf(taskReport.name()));
	if (checkReportExists != null) {
	    if (reportNewDTO != null && reportNewDTO.getNote() != null)
		checkReportExists.setNote(reportNewDTO.getNote());
	    return TaskRes.build(checkReportExists, TaskMessType.OK, "Cập nhật báo cáo công việc thành công");
	}
	try {
	    //Set up task report result for return
	    taskReportResult.setTaskId(task.getTaskId());
	    taskReportResult.setReportAt(LocalDateTime.now());
	    if (reportNewDTO != null && reportNewDTO.getNote() != null)
		taskReportResult.setNote(reportNewDTO.getNote());

	    switch (taskReport) {
	    case ARRIVED: {
		long minutesDiff = ChronoUnit.MINUTES.between(LocalDateTime.now(dateTimeZone),
			task.getSchedule().getStartDate());
		if (minutesDiff > DURATION_MINUTES_TIMES_STAFF_START_REPORT.getNum())
		    return TaskRes.build(checkReportExists, TaskMessType.REJECT_REPORT_TASK,
			    		"Tiến trình báo cáo chưa mở để bạn bắt đầu. Bạn sẽ được mở quyền báo cáo cho công việc tại thời điểm "
				         + dateFormat.format(Date.from(task.getSchedule().getStartDate()
					 .minusMinutes(DURATION_MINUTES_TIMES_STAFF_START_REPORT.getNum())
					 .atZone(dateTimeZone).toInstant())));

		task.setTaskStatus(TaskStatus.ARRIVED);
		task.getSchedule().setStatus(ScheduleStatus.INCOMING);
		taskReportResult.setTaskStatus(task.getTaskStatus());

		// TODO: NOTI STAFF ARRIVED TO CUSTOMER
		TaskBuildupService.createAndSendNotification(
			task,
			notiTitleForTaskStatus,
			"Nhân viên " + task.getStaff().getStaffInfo().getFullName() + "\"Đang đến\"",
			String.valueOf(task.getSchedule().getCustomerId()));
		
		break;
	    }
	    case DOING: {
		// Check if status arrvived is passed through ?
		TaskReport checkArrivedReportExists = taskReportRepo.findByTaskIdAndTaskStatus(task.getTaskId(),
			TaskStatus.ARRIVED);
		if (checkArrivedReportExists == null)
		    return TaskRes.build(taskReportResult, TaskMessType.REJECT_REPORT_TASK, "Báo cáo trạng thái \"Đã Đến\" trước !");

		task.setTaskStatus(TaskStatus.DOING);
		task.getSchedule().setStatus(ScheduleStatus.INCOMING);
		taskReportResult.setTaskStatus(task.getTaskStatus());

		// TODO: NOTI STAFF DOING TO CUSTOMER
		TaskBuildupService.createAndSendNotification(
			task,
			notiTitleForTaskStatus,
			"Nhân viên " + task.getStaff().getStaffInfo().getFullName() + "\"Đang làm việc\"",
			String.valueOf(task.getSchedule().getCustomerId()));
		
		break;
	    }
	    case DONE: {
		// Chedup befor report Done
		TaskReport checkArrivedReportExists = taskReportRepo.findByTaskIdAndTaskStatus(task.getTaskId(),
			TaskStatus.ARRIVED);
		if (checkArrivedReportExists == null)
		    return TaskRes.build(taskReportResult, TaskMessType.REJECT_REPORT_TASK,
			    "Hãy báo cáo cho trạng thái \"Đã Đến\"");
		TaskReport checkDoingReportExists = taskReportRepo.findByTaskIdAndTaskStatus(task.getTaskId(),
			TaskStatus.DOING);
		if (checkDoingReportExists == null)
		    return TaskRes.build(taskReportResult, TaskMessType.REJECT_REPORT_TASK,
			    "Hãy báo cáo cho trạng thái \"Đang làm việc\"");
		if (LocalDateTime.now().isBefore(task.getSchedule().getEndDate()))
		    return TaskRes.build(checkReportExists, TaskMessType.REJECT_REPORT_TASK,
			    "Bạn sẽ được phép bắt đầu báo cáo trạng thái \"Đã hoàn thành\" tại thời điểm khách hàng yêu cầu kết thúc công việc : "
				    + dateFormat.format(Date
					    .from(task.getSchedule().getEndDate().atZone(dateTimeZone).toInstant())));

		// Check for return service type
		boolean isReturnService = serviceInUsed.getGroupType().equals("RETURN_SERVICE");
		if (isReturnService) {
		    Integer quantity = null;
		    if (reportNewDTO == null || reportNewDTO.getQtyOfGroupReturn() == null)
			return TaskRes.build(taskReportResult, TaskMessType.REJECT_REPORT_TASK,
				"Trước khi báo cáo hãy điền giá trị khối lượng cho loại dịch vụ thuộc \"Gửi trả\"");
		    quantity = reportNewDTO.getQtyOfGroupReturn();
		    if (!(quantity <= userUsage.getRemaining() && quantity > 0))
		 			return TaskRes.build(taskReportResult, TaskMessType.REJECT_REPORT_TASK,
		 				"Oops, Số lượng còn lại trong gói bạn chọn chỉ còn " + userUsage.getRemaining()
		 					+ ". Hãy điền giá trị số lượng trong khoảng số lượng mà bạn sở hữu ");
		    if (!(serviceInUsed.getMin() == 0 && serviceInUsed.getMax() == 0)) {
			if (quantity < serviceInUsed.getMin())
			    return TaskRes.build(taskReportResult, TaskMessType.REJECT_REPORT_TASK,
				    "Điền giá trị số lượng cho loại dịch vụ \"Gửi trả\". Hãy điền giá trị số lượng trong khoảng tối thiểu là "
					    + serviceInUsed.getMin() + " - " + serviceInUsed.getMax() + "\nLớn hơn khoảng này bạn sẽ phải trả thêm số tiền cho lượng dư thêm !");
		    }
		    task.getSchedule().setQuantityRetrieve(quantity);
		}

		task.setTaskStatus(TaskStatus.DONE);
		task.getSchedule().setStatus(ScheduleStatus.DONE);
		taskReportResult.setTaskStatus(task.getTaskStatus());
		int newQuantityRemaining = userUsage.getRemaining() - task.getSchedule().getQuantityRetrieve();
		userUsage.setRemaining(newQuantityRemaining < 0 ? 0 : newQuantityRemaining);

		// TODO: NOTI STAFF DONE TO CUSTOMER
		TaskBuildupService.createAndSendNotification(
			task,
			notiTitleForTaskStatus,
			"Nhân viên " + task.getStaff().getStaffInfo().getFullName() + "\"Đã hoàn thành công việc\"",
			String.valueOf(task.getSchedule().getCustomerId()));
		
		break;
	    }
	    }
	    taskRepo.save(task);
	    scheduleRepo.save(task.getSchedule());
	    taskReportResult = taskReportRepo.save(taskReportResult);
 
	} catch (Exception e) {
	    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
	    e.printStackTrace();
	    return TaskRes.build(taskReportResult, TaskMessType.REJECT_REPORT_TASK,
		    "Có lỗi xảy ra ! Báo cáo thất bại !");
	}
	return TaskRes.build(taskReportResult, TaskMessType.OK, "Báo cáo thành công !");
    }

    //MARKUP === SETTING NOTIFICATION===
    public static void createAndSendNotification(Task task, String title, String mess, String userId) {
	// TODO: BUILD MESSAGE
	// TODO SEND NOTIFICATION
    }
    
   //MARKUP ===CREATE EVENTS===
    private void createEventSendNotiWhenTimeComing(Task theTask, LocalDateTime timeStartTask) {
	ZonedDateTime timeStartTaskZone = timeStartTask.atZone(dateTimeZone);
	Instant timeSendNotiInstant = timeStartTaskZone.toInstant();

	Runnable runnableTask = new Runnable() {
	    @Override
	    public void run() {
		Task task = taskRepo.findById(theTask.getTaskId()).get();
		if (task.getStaffId() == null) {
		    task.setTaskStatus(TaskStatus.CANCELLED_CAUSE_NOT_FOUND_STAFF);
		    task.setTaskNote("Không tìm thấy nhân viên nào !");
		    Schedule schedule = scheduleRepo.findById(task.getScheduleId()).get();
		    schedule.setStatus(ScheduleStatus.CANCEL);
		    scheduleRepo.save(schedule);
		    taskRepo.save(task);

		  //TODO: NOTI FOR NOT FOUNDED STAFF TO CUSTOMER
		    TaskBuildupService.createAndSendNotification(
			    task,
			    "Hủy lịch",
			    "Hủy lịch do không tìm thấy nhân viên",
			    String.valueOf(task.getSchedule().getCustomerId()));
		    
		    log.info("Task {} closed at {} staff is null", task.getTaskId(), dateFormat.format(new Date()));
		}
		if (task.getStaffId() != null) {
		    
		  //TODO: NOTI FOR STAFF IS COMING TO CUSTOMER
		    TaskBuildupService.createAndSendNotification(
			    task,
			    notiTitleForTaskStatus,
			    "Nhân viên đang sắp tới phục vụ bạn. Mở cửa cho nhân viên khi tới nhé",
			    String.valueOf(task.getSchedule().getCustomerId()));
		    
		    log.info("Task {} upcomging - staff not null - send at {}", task.getTaskId(), dateFormat.format(new Date()));
		}
	    }
	};
	ScheduledFuture<?> taskEvent = taskScheduler.schedule(runnableTask, timeSendNotiInstant);
	if (eventNotiList.get(theTask.getTaskId()) == null) {
	    List<ScheduledFuture<?>> scheduleFuterOfTaskId = new ArrayList<>();
	    eventNotiList.put(theTask.getTaskId(), scheduleFuterOfTaskId);
	}
	eventNotiList.get(theTask.getTaskId()).add(taskEvent);
	
	log.info("Task {} create event send noti when incoming task - will send at {} ", 
		theTask.getTaskId(), LocalDateTime.ofInstant(timeSendNotiInstant, dateTimeZone));
    }

    private void createEventSendNotiUpcomingTask(Task theTask, LocalDateTime timeStartTask, int periodHourBeforeFrMinutess) {	
	ZonedDateTime timeStartTaskZone = timeStartTask.atZone(dateTimeZone).minusMinutes(periodHourBeforeFrMinutess);
	Instant timeSendNotiInstant = timeStartTaskZone.toInstant();

	Runnable runnableTask = new Runnable() {

	    @Override
	    public void run() {
		Task task = taskRepo.findById(theTask.getTaskId()).get();
		
		if (task.getStaffId() == null) {
		    //TODO: NOTI TIME WORKING UPCOMING BUT NOT FOUND STAFF TO CUSTOMER
		    TaskBuildupService.createAndSendNotification(
			    task,
			    notiTitleForTaskStatus,
			    "Vẫn chưa tìm được nhân viên. Hãy đợi thêm.",
			    String.valueOf(task.getSchedule().getCustomerId()));
		  
		    log.info("Task {} upcoming noti send - staff null - sent at {}", task.getTaskId(),
			    dateFormat.format(new Date()));
		}
		if (task.getStaffId() != null) {
		    task.setTaskStatus(TaskStatus.INCOMING);
		    Schedule schedule = scheduleRepo.findById(task.getScheduleId()).get();
		    schedule.setStatus(ScheduleStatus.INCOMING);
		    scheduleRepo.save(schedule);
		    taskRepo.save(task);
		    
		    //TODO: NOTI TIME WORRKING UPCOMING TO STAFF AND CUSTOMER
		    TaskBuildupService.createAndSendNotification(
			    task,
			    notiTitleForTaskStatus,
			    "Nhân viên sắp đến. Hãy mở cửa nhé !",
			    String.valueOf(task.getSchedule().getCustomerId()));
		    TaskBuildupService.createAndSendNotification(
			    task,
			    notiTitleForTaskStatus,
			    "Hãy chuẩn bị đến giờ làm việc tại nhà khách hàng " + task.getSchedule().getCustomerId()
				    + " vào lúc "
				    + dateFormat.format(Date.from(timeStartTask.atZone(dateTimeZone).toInstant())),
			    String.valueOf(task.getSchedule().getStaffId()));
		    
		    log.info("Task {}  upcoming noti send - staff not null - sent at {}", task.getTaskId(), dateFormat.format(new Date()));
		}
	    }
	};
	ScheduledFuture<?> taskEvent = taskScheduler.schedule(runnableTask, timeSendNotiInstant);
	if (eventNotiList.get(theTask.getTaskId()) == null) {
	    List<ScheduledFuture<?>> scheduleFuterOfTaskId = new ArrayList<>();
	    eventNotiList.put(theTask.getTaskId(), scheduleFuterOfTaskId);
	}
	eventNotiList.get(theTask.getTaskId()).add(taskEvent);
	
	log.info("Task {} create event send noti for upcoming schedule - will send at {}",
		theTask.getTaskId(), LocalDateTime.ofInstant(timeSendNotiInstant, dateTimeZone) );
    }
    
    private void createEventForReportTask(Task theTask, LocalDateTime timeStartWorking, LocalDateTime timeEndWorking) {
	/*1. Allow report task befor timestart is DURATION_MINUTES_TIMES_STAFF_START_REPORT = 15
	 Duration for report status ARRIVED is DURATION_MINUTES_TIMES_STAFF_START_REPORT + time start working + DURATION_MINUTES_TIMES_STAFF_START_REPORT
	 Minus proficient score for not report task after time start working a duration DURATION_MINUTES_TIMES_STAFF_START_REPORT mininutes 
	 Then the staff will be minus a score MINUS_POINTS_FOR_NOT_COMPLETE_REPORT_TASK = 25 by the system event but still allow report this status before end time working
	2. Trigger event for cancel task when staff not report for status Doing at the endtime working
	3. Trigger event for auto report Done status for the task has already report Doing at the time after end time working is DURATION_HOURS_SYST_AUTO_DONE_TASK hours
	4. Trigger event cancel task when staff not report for status DONE after the end of day at 00 PM every day and minus the score MINUS_POINTS_FOR_NOT_COMPLETE_REPORT_TASK
	*/
   	ZonedDateTime timeStartWorkingZone = timeStartWorking.atZone(dateTimeZone);
   	ZonedDateTime timeEndWorkingZone = timeEndWorking.atZone(dateTimeZone);
  	ZonedDateTime todayAtMidnight = LocalDate.now().atTime(LocalTime.MIDNIGHT).atZone(dateTimeZone);
   	//Trigger 1
   	Instant timeMinusScoreForNotReportArrived = timeStartWorkingZone.plusMinutes(DURATION_MINUTES_TIMES_STAFF_START_REPORT.getNum()).toInstant(); 
   	//Trigger 2
   	Instant timeCancelTaskForNotReportDoing = timeEndWorkingZone.toInstant();
   	//Trigger 3
   	Instant timeAutoDoneTask = timeEndWorkingZone.plusMinutes(DURATION_HOURS_SYST_AUTO_DONE_TASK.getNum()).toInstant();
   	//Trigger 4
   	Instant timeCancelTaskForNotReportDone = todayAtMidnight.toInstant();
 
   	//Trigger 1
	{
	    Runnable eventMinusScoreForNotReportArrived = new Runnable() {
		@Override
		public void run() {
		    Task task = taskRepo.findById(theTask.getTaskId()).get();
		    TaskReport arrivedReport = taskReportRepo.findByTaskIdAndTaskStatus(task.getTaskId(), TaskStatus.ARRIVED);
		    if (task.getStaffId() != null && !task.getTaskStatus().equals("CANCELLED") && arrivedReport == null) {
			Staff staff = staffRepo.findById(task.getStaffId()).get();
			bannedStaff(staff);
			staff.setProfiencyScore(staff.getProfiencyScore() - MINUS_POINTS_FOR_NOT_COMPLETE_REPORT_TASK.getNum());
			staff.setProfiencyScore(staff.getProfiencyScore() < 0 ? 0 : staff.getProfiencyScore());
			staffRepo.save(staff);
			
			// TODO: NOTI MINUS THE PROFICIENT SCORE FOR REPORT ARRIVED TO STAFF
			 TaskBuildupService.createAndSendNotification(
				    task,
				    notiTitleForTaskStatus,
				    "Trừ " + MINUS_POINTS_FOR_NOT_COMPLETE_REPORT_TASK + " do trễ hẹn báo cáo trạng thái \"Đã đến\"",
				    String.valueOf(task.getSchedule().getStaffId()));
			 
			log.info("Task {} minus score for not report Arrived - Sent at {}", task.getTaskId(),
				dateFormat.format(new Date()));
		    }
		}
	    };
	    ScheduledFuture<?> taskEvent = taskScheduler.schedule(eventMinusScoreForNotReportArrived, timeMinusScoreForNotReportArrived);
	    if (eventNotiList.get(theTask.getTaskId()) == null) {
		List<ScheduledFuture<?>> scheduleFuterOfTaskId = new ArrayList<>();
		eventNotiList.put(theTask.getTaskId(), scheduleFuterOfTaskId);
	    }
	    eventNotiList.get(theTask.getTaskId()).add(taskEvent);

	    log.info("Task {} create event minus score for not report arrived - will sent {}",theTask.getTaskId(),
		    timeMinusScoreForNotReportArrived);
	}

	//Trigger 2
	{
	    Runnable eventTaskForNotReportDoing = new Runnable() {
		@Override
		public void run() {
		    Task task = taskRepo.findById(theTask.getTaskId()).get();
		    TaskReport doingReport = taskReportRepo.findByTaskIdAndTaskStatus(task.getTaskId(), TaskStatus.DOING);
		    
		    if (task.getStaffId() != null && !task.getTaskStatus().equals("CANCELLED") && doingReport == null ) {
			Staff staff = staffRepo.findById(task.getStaffId()).get();
			staff.setProfiencyScore(staff.getProfiencyScore() - MINUS_POINTS_FOR_NOT_COMPLETE_REPORT_TASK.getNum());
			staff.setProfiencyScore(staff.getProfiencyScore() < 0 ? 0 : staff.getProfiencyScore());
			bannedStaff(staff);
			Schedule schedule = scheduleRepo.findById(task.getScheduleId()).get();
			schedule.setStatus(ScheduleStatus.CANCEL);
			schedule.setNote(schedule.getNote()
				+ " - Lịch bị hủy do nhân viên không tới nhà !");
			task.setTaskStatus(TaskStatus.CANCELLED_BY_STAFF);
			task.setTaskNote("Hủy công việc vì không báo cáo đầy đủ cho trạng thái \"Đang làm việc\" đúng khung giờ quy định !");
			staffRepo.save(staff);
			taskRepo.save(task);
			scheduleRepo.save(schedule);

			// TODO: NOTI CANCEL TASK FOR NOT REPORT DOING TO STAFF
			 TaskBuildupService.createAndSendNotification(
				    task,
				    "Hủy lịch",
				    "Hủy lịch do không báo cáo tiến trình \"Đang làm việc\" ",
				    String.valueOf(task.getSchedule().getStaffId()));
			
			 // TODO: NOTI CANCEL SCHEDULE WHEN STAFF NOT REPORT DOING TO CUSTOMER
			 TaskBuildupService.createAndSendNotification(
				    task,
				    "Hủy công việc",
				    "Hủy lịch do nhân viên không báo cáo tiến trình làm việc.",
				    String.valueOf(task.getSchedule().getCustomerId()));

			log.info("Task {} has cancelled task for not report DOING - staff not null - at {}",
				task.getTaskId(), dateFormat.format(new Date()));
		    }
		}
	    };
	    ScheduledFuture<?> taskEvent = taskScheduler.schedule(eventTaskForNotReportDoing, timeCancelTaskForNotReportDoing);
	    if (eventNotiList.get(theTask.getTaskId()) == null) {
		List<ScheduledFuture<?>> scheduleFuterOfTaskId = new ArrayList<>();
		eventNotiList.put(theTask.getTaskId(), scheduleFuterOfTaskId);
	    }
	    eventNotiList.get(theTask.getTaskId()).add(taskEvent);

	    log.info("Task {} create event cancel task for not report doing - send at {}",
		  theTask.getTaskId(), timeCancelTaskForNotReportDoing);    
	}
	// trigger 3
	{
	    Runnable eventAutoDoneTask = new Runnable() {
		@Override
		public void run() {
		    Task task = taskRepo.findById(theTask.getTaskId()).get();
		    TaskReport doneReport = taskReportRepo.findByTaskIdAndTaskStatus(task.getTaskId(), TaskStatus.DONE);
		    TaskReport doingReport = taskReportRepo.findByTaskIdAndTaskStatus(task.getTaskId(), TaskStatus.DOING);
		    if (task.getStaffId() != null && !task.getTaskStatus().equals("CANCELLED") && doneReport == null && doingReport != null) {
			Schedule schedule = scheduleRepo.findById(task.getScheduleId()).get();
			schedule.setStatus(ScheduleStatus.DONE);
			UserUsage userUsage = userUsageRepo.findById(task.getSchedule().getUserUsageId()).get();
			int newQuantityRemaining = userUsage.getRemaining() - task.getSchedule().getQuantityRetrieve();
			userUsage.setRemaining(newQuantityRemaining < 0 ? 0 : newQuantityRemaining);
			scheduleRepo.save(schedule);
			userUsageRepo.save(userUsage);
			
			// TODO: NOTI TO STAFF TO REPORT STATUS DONE
			TaskBuildupService.createAndSendNotification(
				    task,
				    notiTitleForTaskStatus,
				    "Vui lòng báo cáo tiến trình hoàn thành. Nếu không bạn sẽ bị hủy công việc do không báo cáo tiến trình đầy đủ",
				    String.valueOf(task.getSchedule().getStaffId()));
			// TODO: NOTI REPORT STATUS DONE TO CUSTOMER  
			TaskBuildupService.createAndSendNotification(
				    task,
				    notiTitleForTaskStatus,
				    "Lịch làm việc đã hoàn thành, vui lòng kiểm tra lại trước 24 giờ đêm nay nếu có khiếu nại !\nSau thời gian này chúng tôi không nhận khiếu nại về báo cáo tiến trình làm việc gì thêm !",
				    String.valueOf(task.getSchedule().getStaffId()));
			
			log.info("Task {} auto done report - staff not null - sent at {}", task.getTaskId(),
				dateFormat.format(new Date()));
		    }
		}
	    };
	    ScheduledFuture<?> taskEvent = taskScheduler.schedule(eventAutoDoneTask, timeAutoDoneTask);
	    if (eventNotiList.get(theTask.getTaskId()) == null) {
		List<ScheduledFuture<?>> scheduleFuterOfTaskId = new ArrayList<>();
		eventNotiList.put(theTask.getTaskId(), scheduleFuterOfTaskId);
	    }
	    eventNotiList.get(theTask.getTaskId()).add(taskEvent);

	    log.info("Task {} create event auto done - will send at {}",
		    theTask.getTaskId(), timeAutoDoneTask);

	}
	// trigger 4
	{
	    Runnable eventCancelTaskForNotReportDone = new Runnable() {
		@Override
		public void run() {
		    Task task = taskRepo.findById(theTask.getTaskId()).get();
		    TaskReport doneReport = taskReportRepo.findByTaskIdAndTaskStatus(task.getTaskId(), TaskStatus.DONE);
		    TaskReport doingReport = taskReportRepo.findByTaskIdAndTaskStatus(task.getTaskId(), TaskStatus.DOING);
		    if (task.getStaffId() != null && !task.getTaskStatus().equals("CANCELLED") && doneReport == null && doingReport != null) {
			Staff staff = staffRepo.findById(task.getStaffId()).get();
			staff.setProfiencyScore(staff.getProfiencyScore() - MINUS_POINTS_FOR_NOT_COMPLETE_REPORT_TASK.getNum());
			staff.setProfiencyScore(staff.getProfiencyScore() < 0 ? 0 : staff.getProfiencyScore());
			bannedStaff(staff);
			task.setTaskStatus(TaskStatus.CANCELLED_BY_STAFF);
			task.setTaskNote("Buộc phải hủy công việc vì bạn không báo cáo cho trạng thái \"ĐÃ HOÀN THÀNH\" đúng khung giờ quy định !");
			staffRepo.save(staff);
			taskRepo.save(task);
						
			// TODO: NOTI TO STAFF FOR NOT REPORT STATUS DONE
			TaskBuildupService.createAndSendNotification(
				    task,
				    "Hủy công việc",
				    "Hủy công việc do không hoàn thành báo cáo ",
				    String.valueOf(task.getSchedule().getStaffId()));
			
			log.info("Task {} cancel task for not report done  - staff not null - sent at {}", 
				task.getTaskId(), dateFormat.format(new Date()));
		    }
		}
	    };
	    ScheduledFuture<?> taskEvent = taskScheduler.schedule(eventCancelTaskForNotReportDone, timeCancelTaskForNotReportDone);
	    if (eventNotiList.get(theTask.getTaskId()) == null) {
		List<ScheduledFuture<?>> scheduleFuterOfTaskId = new ArrayList<>();
		eventNotiList.put(theTask.getTaskId(), scheduleFuterOfTaskId);
	    }
	    eventNotiList.get(theTask.getTaskId()).add(taskEvent);

	    log.info("Task {} create event cancel task for not report DONE - will send at {}",
		    theTask.getTaskId(), timeCancelTaskForNotReportDone);
	}
	
   	log.info("Task {} Trigger 1 timeMinusScoreForNotReportArrived call at {}", theTask.getTaskId(), LocalDateTime.ofInstant(timeCancelTaskForNotReportDone, dateTimeZone));
   	log.info("Task {} Trigger 2 timeCancelTaskForNotReportDoing call at {}", theTask.getTaskId(), LocalDateTime.ofInstant(timeMinusScoreForNotReportArrived, dateTimeZone));
   	log.info("Task {} Trigger 3 timeAutoDoneTask call at {}", theTask.getTaskId(), LocalDateTime.ofInstant(timeAutoDoneTask, dateTimeZone) );
   	log.info("Task {} Trigger 4 timeCancelTaskForNotReportDone call at {}", theTask.getTaskId(), LocalDateTime.ofInstant(timeCancelTaskForNotReportDone, dateTimeZone));

    }
    
    private void CancelAllEventsByTaskId(int taskToBeCancelId) {
	if (eventNotiList.get(taskToBeCancelId) != null) {
	    eventNotiList.get(taskToBeCancelId).stream().map(event -> event.cancel(true));	    
	    eventNotiList.remove(taskToBeCancelId);
	}
    }
    
    private void bannedStaff(Staff staff) {
	if(staff.getProfiencyScore() == 0)
	    staff.setBanned(true);
    }
    
    public Schedule checkIsDuplicateTask(Task newTask, Staff staff){
	Service service = servRepo.findByServiceId(newTask.getSchedule().getServiceId()).get();
	String serviceGrTypeOfNewTask  = service.getGroupType();
	Schedule scheduleOfNewTask = newTask.getSchedule();
	List<Schedule> schedules = scheduleRepo.findAllCurrentTaskByStaffId(staff.getStaffId());
	if(schedules.isEmpty()) {
	    return null;
	}
	Schedule dupSchedule = null;
	switch (serviceGrTypeOfNewTask) {
	case "HOURLY_SERVICE": {
	    dupSchedule = schedules.stream().filter(x -> {
			Boolean isDuplicate = false;
			//Check duplicate with the period time of hourly service
			isDuplicate = servRepo.findByServiceId(x.getServiceId()).get().getGroupType().equals("HOURLY_SERVICE") 
			&& 
			(
	        		(x.getStartDate().isAfter(scheduleOfNewTask.getEndDate()) || x.getStartDate().equals(scheduleOfNewTask.getEndDate()))
	        		&& 
	        		(x.getEndDate().isBefore(scheduleOfNewTask.getStartDate()) || x.getEndDate().equals(scheduleOfNewTask.getStartDate()))
			 );
			
			if(isDuplicate)
			    return isDuplicate;
			//check duplicate by check if the new period time of new schedule cover any start time or end time
			isDuplicate = !servRepo.findByServiceId(x.getServiceId()).get().getGroupType().equals("HOURLY_SERVICE")
			&&
			(
				(scheduleOfNewTask.getStartDate().isBefore(x.getStartDate()) || scheduleOfNewTask.getStartDate().isEqual(x.getStartDate()))
				&& (scheduleOfNewTask.getEndDate().isAfter(x.getStartDate()) || scheduleOfNewTask.getEndDate().isEqual(x.getStartDate()))
				||
				(scheduleOfNewTask.getStartDate().isBefore(x.getEndDate()) || scheduleOfNewTask.getStartDate().isEqual(x.getEndDate()))
				&& (scheduleOfNewTask.getEndDate().isAfter(x.getEndDate()) || scheduleOfNewTask.getEndDate().isEqual(x.getEndDate()))
			);
			
			return isDuplicate;
		    }).findAny().orElse(null);
		   
	    return dupSchedule;
		   
	}
	case "DELIVERY_SERVICE", "RETURN_SERVICE" : {
	    dupSchedule = schedules.stream().filter(x -> {
			Boolean isDuplicate = false;
			//check if any period time of hourly service contain the start time or end time of new schedule
			isDuplicate = servRepo.findByServiceId(x.getServiceId()).get().getGroupType().equals("HOURLY_SERVICE") 
			&& 
			(
				(x.getStartDate().isBefore(scheduleOfNewTask.getStartDate()) || x.getStartDate().isEqual(scheduleOfNewTask.getStartDate()))
				&& (x.getEndDate().isAfter(scheduleOfNewTask.getStartDate()) || x.getEndDate().isEqual(scheduleOfNewTask.getStartDate()))
				||
				(x.getStartDate().isBefore(scheduleOfNewTask.getEndDate()) || x.getStartDate().isEqual(scheduleOfNewTask.getEndDate()))
				&& (x.getEndDate().isAfter(scheduleOfNewTask.getEndDate()) || x.getEndDate().isEqual(scheduleOfNewTask.getEndDate()))	
			 );
			
			if(isDuplicate)
			    return isDuplicate;
			
			//check if any start time or end time equal with any start time or end time of new schedule
			isDuplicate = !servRepo.findByServiceId(x.getServiceId()).get().getGroupType().equals("HOURLY_SERVICE")
			&&
			(
				 x.getStartDate().isEqual(scheduleOfNewTask.getStartDate()) 
				 || 
				 x.getStartDate().isEqual(scheduleOfNewTask.getEndDate())
				 ||
				 x.getEndDate().isEqual(scheduleOfNewTask.getStartDate())
				 ||
				 x.getEndDate().isEqual(scheduleOfNewTask.getStartDate())			
			);
			
			return isDuplicate;
		    }).findAny().orElse(null);
		   
	    return dupSchedule;
	}
	}
	return dupSchedule;
    }   

}
