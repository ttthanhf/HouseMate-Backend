package housemate.services;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import housemate.constants.ScheduleStatus;
import static housemate.constants.ServiceConfiguration.*;
import housemate.constants.AccountStatus;
import housemate.constants.Enum.TaskMessType;
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
import housemate.utils.AuthorizationUtil;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class TaskService {

    @Autowired
    private TaskReposiotory taskRepo;

    @Autowired
    private TaskBuildupService taskBuildupServ;

    @Autowired
    private ScheduleRepository scheduleRepo;

    @Autowired
    private StaffRepository staffRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private TaskReportRepository taskReportRepo;

    @Autowired
    private ImageRepository imgRepo;

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private AuthorizationUtil authorizationUtil;

    private final ZoneId dateTimeZone = ZoneId.of("Asia/Ho_Chi_Minh");
    
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    // VIEW TASK PENDING APPLICATION
    public ResponseEntity<?> getAllTaskInPendingApplication(Optional<Sort.Direction> orderByCreatedDirection,
	    Optional<Integer> page, Optional<Integer> size) {
	int pageNo = page.orElse(0);
	int pageSize = size.orElse(9);
	Sort.Direction direction = orderByCreatedDirection.orElse(Direction.ASC);
	if (pageNo < 0 || pageSize < 1)
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Trang bắt đầu không được nhỏ hơn 1 !");

	// setting sort and pageable
	Sort sort = Sort.by(direction, "createdAt");
	Pageable pagableTaskList = pageNo == 0 ? PageRequest.of(0, pageSize, sort)
		: PageRequest.of(pageNo - 1, pageSize, sort);
	Page<Task> taskList = taskRepo.findAllByTaskStatus(TaskStatus.PENDING_APPLICATION, pagableTaskList);

	Page<TaskViewDTO> taskViewList = Page.empty(pagableTaskList);

	if (taskList.isEmpty())
	    return ResponseEntity.ok(List.of());

	Function<Task, TaskViewDTO> convertInToTaskViewDTO = task -> {
	    return taskBuildupServ.convertIntoTaskViewDtoFromTask(task);
	};
	taskViewList = taskList.map(convertInToTaskViewDTO);

	return ResponseEntity.ok(taskViewList);
    }
    
    // VIEW TASK UP COMING WORING BY STAFF
    public ResponseEntity<?> getAllTaskForStaffByTaskStatus(HttpServletRequest request, @Nullable TaskStatus taskStatus,
	    Optional<Integer> page, Optional<Integer> size) {
	int staffId = authorizationUtil.getUserIdFromAuthorizationHeader(request);

	Staff staff = staffRepo.findById(staffId).orElse(null);
	if (staff == null)
	    return ResponseEntity.badRequest().body("Staff not exists");

	int pageNo = page.orElse(0);
	int pageSize = size.orElse(9);
	if (pageNo < 0 || pageSize < 1)
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Trang bắt đầu không được nhỏ hơn 1 !");

	// setting sort and pageable
	Sort sort = Sort.by(Sort.Direction.DESC, "receivedAt");

	Pageable pagableTaskList = pageNo == 0 ? PageRequest.of(0, pageSize, sort)
		: PageRequest.of(pageNo - 1, pageSize, sort);
	
	Page<Task> taskListForStaff;
	Page<TaskViewDTO> taskViewListForStaff;
	
	if (taskStatus.equals(TaskStatus.CANCELLED)) {
	    taskStatus = null;
	    taskListForStaff = taskRepo.findAllByTaskStatusAndStaffId(staffId, taskStatus, pagableTaskList);
	    if (!taskListForStaff.getContent().isEmpty()) {
		 List<Task> filteredTaskList = taskListForStaff
		                .stream()
		                .filter(t -> t.getTaskStatus().name().contains("CANCELLED"))
		                .collect(Collectors.toList());
		        taskListForStaff = new PageImpl<>(filteredTaskList, pagableTaskList, filteredTaskList.size());
	    }
		
	} else
	    taskListForStaff = taskRepo.findAllByTaskStatusAndStaffId(staffId, taskStatus, pagableTaskList);
	
	taskViewListForStaff = Page.empty(pagableTaskList);

	if (taskListForStaff.isEmpty())
	    return ResponseEntity.ok(List.of());

	Function<Task, TaskViewDTO> convertInToTaskViewDTO = task -> {
	    return taskBuildupServ.convertIntoTaskViewDtoFromTask(task);
	};
	taskViewListForStaff = taskListForStaff.map(convertInToTaskViewDTO);

	return ResponseEntity.ok(taskViewListForStaff);
    }

    // VIEW TASK IN DETAILS
    public ResponseEntity<?> getTaskInDetails(int taskId) {
	Task task = taskRepo.findById(taskId).orElse(null);
	if (task == null)
	    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Thông tin về công việc này không tồn tại !");
	TaskViewDTO taskViewInDetails = taskBuildupServ.convertIntoTaskViewDtoFromTask(task);
	return ResponseEntity.ok().body(taskViewInDetails);
    }

    public ResponseEntity<?> getTaskViewInDetailsForCustomerByScheduleId(HttpServletRequest request, int scheduleId) {
	Schedule schedule = scheduleRepo.findById(scheduleId).orElse(null);
	if (schedule == null)
	    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lịch này không tồn tại !");
	int scheduleOwner = authorizationUtil.getUserIdFromAuthorizationHeader(request);
	Role role = Role.valueOf(authorizationUtil.getRoleFromAuthorizationHeader(request));
	if (!role.equals(Role.ADMIN) && !(role.equals(Role.CUSTOMER) && scheduleOwner == schedule.getCustomerId()))
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bạn không phải chủ sở hữu của lịch này !");

	long dayDiff = ChronoUnit.DAYS.between(LocalDateTime.now(dateTimeZone), schedule.getStartDate());
	
	Task task = null;
	
	if(staffRepo.findById(schedule.getStaffId()).orElse(null) != null) {
	    task = taskRepo.findByScheduleIdAndStaffId(scheduleId, schedule.getStaffId());
	}
	else
	    task = taskRepo.findExistingTaskForSchedule(scheduleId);

	if (schedule.getStatus().equals(ScheduleStatus.PROCESSING) && task == null && dayDiff > 1)
	    return ResponseEntity.status(HttpStatus.NOT_FOUND)
		    .body("Thông tin công việc cho lịch này sẽ có khi lịch đến ngày ! Hãy đợi !");
	else if (schedule.getStatus().equals(ScheduleStatus.CANCEL) && task == null)
	    return ResponseEntity.status(HttpStatus.NOT_FOUND)
		    .body("Lịch này đã bị hủy từ trước. Sẽ không có thông tin về công việc của lịch này !");
	else if (task == null)
	    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy thông tin công việc cho lịch này !");

	TaskViewDTO taskViewDto = taskBuildupServ.convertIntoTaskViewDtoFromTask(task);

	return ResponseEntity.ok().body(taskViewDto);
    }

    // CREATE TASK BY CUSTOMER
    public ResponseEntity<?> createNewTask(HttpServletRequest request, int scheduleId) {
	Schedule schedule = scheduleRepo.findById(scheduleId).orElse(null);
	int customerIdRequestCreate = authorizationUtil.getUserIdFromAuthorizationHeader(request);
	if (schedule == null)
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Không thể lên lịch làm việc cho lịch không tồn tại !");
	if (customerIdRequestCreate != schedule.getCustomerId())
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bạn không phải là chủ sở hữu của lịch này !");
	if (schedule.getStatus().equals(ScheduleStatus.CANCEL) && taskRepo.findByScheduleIdAndTaskStatus(scheduleId, TaskStatus.CANCELLED_CAUSE_NOT_FOUND_STAFF) != null)
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
		    "Lịch này đã bị hủy vì không tìm thấy nhân viên tại thời"
		    + " điểm lịch của bạn đến hẹn làm việc ! Vui lòng tạo lịch"
		    + " mới để hệ thống lên lịch làm việc cho bạn !");
	if (schedule.getStatus().equals(ScheduleStatus.CANCEL))
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lịch này đã bị hủy ! Vui lòng tạo lịch mới để hệ thống lên lịch làm việc cho bạn !");

	Task createdTask = taskRepo.findExistingTaskForSchedule(scheduleId);
	if (createdTask != null)
	    return ResponseEntity.ok(taskBuildupServ.convertIntoTaskViewDtoFromTask(createdTask));

	List<Task> task = taskBuildupServ.createTaskOnUpComingSchedule(schedule);
	if (task.isEmpty())
	    return ResponseEntity.status(HttpStatus.OK).body(List.of());
	
	List<TaskViewDTO> taskViewDtoList = task.stream()
		.map(x -> taskBuildupServ.convertIntoTaskViewDtoFromTask(x))
		.collect(Collectors.toList());
	
	return ResponseEntity.ok().body(taskViewDtoList);
    }

    // CANCEL TASK
    public ResponseEntity<?> cancelTask(HttpServletRequest request, int scheduleId) {
	int userIdRequestCancel = authorizationUtil.getUserIdFromAuthorizationHeader(request);

	Schedule scheduleToBeCancelled = scheduleRepo.findById(scheduleId).orElse(null);
	if (scheduleToBeCancelled == null)
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Không thể lên lịch làm việc cho lịch không tồn tại !");

	UserAccount userCancel = userRepo.findByUserId(userIdRequestCancel);
	if (userCancel == null)
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tài khoản không tồn tại !");

	TaskRes taskRes = null;
	Task taskToBeCancelled = taskRepo.findExistingTaskForSchedule(scheduleId);
	Role role = userCancel.getRole();
	Customer customer = customerRepo.findById(userCancel.getUserId()).orElse(null);
	LocalDateTime timeStartOfScheduleToBeCancel = scheduleToBeCancelled.getStartDate();
	LocalDateTime timeNow = LocalDateTime.now(dateTimeZone);
	long hoursFrMinutes = ChronoUnit.MINUTES.between(timeNow, timeStartOfScheduleToBeCancel);

	if (role.equals(Role.CUSTOMER) || role.equals(Role.ADMIN)) {
	    if (userIdRequestCancel != scheduleToBeCancelled.getCustomerId())
		    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bạn không có quyền được xóa lịch này !");
	    if (scheduleToBeCancelled.getStatus().equals(ScheduleStatus.CANCEL) && taskRepo
		    .findByScheduleIdAndTaskStatus(scheduleId, TaskStatus.CANCELLED_CAUSE_NOT_FOUND_STAFF) != null)
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
			    "Lịch này đã bị hủy vì không tìm thấy nhân viên tại thời điểm lịch của bạn đến hẹn làm việc ! Vui lòng tạo lịch mới để hệ thống lên lịch làm việc cho bạn !");
	    if (userIdRequestCancel == scheduleToBeCancelled.getCustomerId()
		    && scheduleToBeCancelled.getStatus().equals(ScheduleStatus.CANCEL))
		return ResponseEntity.ok()
			.body("Bạn đã hủy lịch này thày công !\nChú ý bạn, bạn được phép hủy lịch trước giờ làm việc trước "
				+ DURATION_HOURS_CUSTOMER_SHOULD_NOT_CANCEL_TASK.getNum()
				+ " tiếng để đảm bảo nhân viên của chúng tôi sắp xếp được lịch làm việc ."
				+ "\nSau khoảng thời gian này chúng tôi sẽ trừ điểm uy tín của bạn.\nĐiểm uy tín nếu bằng 0 tài khoản sẽ bị cấm bởi hệ thống");
	    if (customer.getAccountStatus().equals(AccountStatus.BANNED))
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body("Tài khoản của bạn đã bị cấm khỏi hệ thống vì đã vượt giới hạn số lần được hủy và điểm uy tín của bạn bằng 0 !");
	   
	    taskToBeCancelled = taskBuildupServ.cancelTaskByRole(Role.CUSTOMER, scheduleToBeCancelled,
		    "Khách hàng " + customer.getCustomerInfo().getFullName() + " đã hủy công việc !");
	    if (taskToBeCancelled == null)
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Có lỗi xảy ra ! Hủy lịch thất bại !");
	    if (hoursFrMinutes < DURATION_HOURS_CUSTOMER_SHOULD_NOT_CANCEL_TASK.getNum()) {
		if (taskToBeCancelled.getStaff() != null) {
		    int subtract = customer.getProfiencyScore() - MINUS_POINTS_FOR_CUSTOMER_CANCEL_TASK.getNum();
		    customer.setProfiencyScore(subtract < 0 ? 0 : subtract);
		    customerRepo.save(customer);
		    taskBuildupServ.bannedStaff(customer.getCustomerId());	     
		}
	    }
	    return ResponseEntity.ok().body("Bạn đã hủy lịch thành công !");
	}
	if (role.equals(Role.STAFF)) {
	    if (userIdRequestCancel != scheduleToBeCancelled.getStaffId())
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bạn không có quyền được xóa lịch này !");
	    taskToBeCancelled = taskBuildupServ.cancelTaskByRole(Role.STAFF, scheduleToBeCancelled,
		    "Nhân viên hủy lịch làm việc !");
	    if (taskToBeCancelled == null)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Có lỗi xảy ra ! Hủy lịch thất bại !");
	    if (hoursFrMinutes < DURATION_HOURS_STAFF_SHOULD_NOT_CANCEL_TASK.getNum()) {
		Staff staff = staffRepo.findById(userCancel.getUserId()).get();
		int subtract = staff.getProfiencyScore() - MINUS_POINTS_FOR_STAFF_CANCEL_TASK.getNum();
		staff.setProfiencyScore(subtract < 0 ? 0 : subtract);
		staffRepo.save(staff);
		taskBuildupServ.bannedStaff(staff.getStaffId());
	    }
	    return ResponseEntity.ok().body("Bạn đã hủy lịch thành công !");
	}
	return ResponseEntity.ok().body("Hủy lịch thành công !");
    }

    // UPDATE TASK TO CHANGE THE TIME
    public ResponseEntity<?> updateTaskTimeWorking(HttpServletRequest request,
	    Schedule scheduleNewTimeWorking) {
	Schedule newSchedule = scheduleRepo.findById(scheduleNewTimeWorking.getScheduleId()).get();
	int customerIdRequestUpdate = authorizationUtil.getUserIdFromAuthorizationHeader(request);
	if (newSchedule == null)
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lịch không tồn tại");
	
	TaskRes taskRes = taskBuildupServ.updateTaskOnScheduleChangeTime(scheduleNewTimeWorking);
	if (taskRes.getMessType().equals(TaskMessType.REJECT_UPDATE_TASK)) {
	    return ResponseEntity.badRequest().body(taskRes.getMessage());
	}

	Map<String, Task> tasksOldAndNew = (Map<String, Task>) taskRes.getObject();
	Task newTask = tasksOldAndNew.get("newTask");
	
	return ResponseEntity.ok().body(newTask);
    }

    // APPROVE TASK
    public ResponseEntity<?> approveStaff(HttpServletRequest request, int taskId) {
	String role = authorizationUtil.getRoleFromAuthorizationHeader(request);
	if (!role.equals(Role.STAFF.name()))
	    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Truy cập thất bại - Chỉ cho phép tài khoản nhân viên mới được ứng tuyển !");
	int staffId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
	Staff staff = staffRepo.findById(staffId).orElse(null);
	if (staff == null)
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Nhân viên không tồn tại !");
	if (staff.getAccountStatus().equals(AccountStatus.BANNED))
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tài khoản của bạn đã bị cấm ! Bạn không được phép mua hàng hay ứng tuyển công việc từ tài khoản này được nữa !");
	Task task = taskRepo.findById(taskId).orElse(null);
	if (task == null)
	    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Công việc này không tồn tại hoặc chưa mở để bạn ứng tuyển");
	if (task.getTaskStatus().name().contains("CANCELLED"))
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bạn không thể ứng tuyển công việc này vì công việc này đã bị hủy !");
	if (task.getStaffId() != null && task.getStaffId() == staffId)
	    return ResponseEntity.ok().body(taskBuildupServ.convertIntoTaskViewDtoFromTask(task));
	//Check duplicate task
	Schedule dupSchedule = taskBuildupServ.checkIsDuplicateTask(task, staff);
	if(dupSchedule != null) 
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
		    .body("Bạn không thể ứng tuyển công việc này !"
		    	+ " Khoảng thời gian làm việc của lịch này đã trùng với khoảng thời gian của lịch công việc khác của bạn "
			    + "có khung giờ bắt đầu ngày " 
			    + dateFormat.format(Date.from(dupSchedule.getStartDate().atZone(dateTimeZone).toInstant())) + " và kết thúc ngày "
			    + dateFormat.format(Date.from(dupSchedule.getEndDate().atZone(dateTimeZone).toInstant()))
			    + " ! ");
	
	if (task.getStaffId() != null && task.getStaffId() != staffId)
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lịch này đã có nhân viên khác ứng tuyển !");
	
	LocalDateTime timeNow = LocalDateTime.now(dateTimeZone);
	LocalDateTime timeStartWorking = task.getSchedule().getStartDate();
	long hoursDiffFrMinutes = ChronoUnit.MINUTES.between(timeNow, timeStartWorking);
	
	if (staff.getProfiencyScore() < BAD_STAFF_PROFICIENT_SCORE.getNum()
		&& hoursDiffFrMinutes > DURATION_HOURS_ALLOW_BAD_STAFF_PROFICENT_SCORE_APPLY.getNum())
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
		    "Bạn không đủ điểm uy tín để ứng tuyển ! Hãy quay lại ứng tuyển trong khoảng thời gian trước lịch làm việc này  "
			    + DURATION_HOURS_ALLOW_BAD_STAFF_PROFICENT_SCORE_APPLY.getNum() + " tiếng !");

	TaskRes taskRes = taskBuildupServ.approveQualifiedStaff(staff, task);
	if (taskRes.getMessType().name().contains("REJECT"))
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(taskRes.getMessage());

	Task approvedTask = (Task) taskRes.getObject();
	TaskViewDTO approvedTaskView = taskBuildupServ.convertIntoTaskViewDtoFromTask(approvedTask);
	return ResponseEntity.ok().body(approvedTaskView);
    }

    //REPORT TASK
    public ResponseEntity<?> reportTaskByStaff(HttpServletRequest request, int taskId, TaskReportType taskReportType, TaskReportNewDTO reportnewDTO) {
	int userReport = authorizationUtil.getUserIdFromAuthorizationHeader(request);
	Role userReportRole = Role.valueOf(authorizationUtil.getRoleFromAuthorizationHeader(request));
	Task taskToBeReported = taskRepo.findById(taskId).orElse(null);

	if (taskToBeReported == null)
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Công việc này không tồn tại !");
	if (!(taskToBeReported.getStaffId() != null && !taskToBeReported.getTaskStatus().name().contains("CANCELLED")
		&& userReportRole.equals(Role.STAFF) && userReport == taskToBeReported.getStaffId()))
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bạn không có quyền được phép báo cáo tiến trình làm việc cho công việc này !");

	TaskRes<TaskReport> taskReportedRes = taskBuildupServ.reportTask(taskToBeReported, taskReportType, reportnewDTO);
	if (taskReportedRes == null)
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Có lỗi xảy ra ! Báo cáo công việc thất bại ! Hãy thử lại !");
	if (taskReportedRes.getMessType().name().contains("REJECT"))
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(taskReportedRes.getMessage());

	return ResponseEntity.ok().body(taskReportedRes.getObject());
    }

    public ResponseEntity<?> getTaskReportListByTask(int taskId) {
	List<TaskReport> taskReport = taskReportRepo.findAllByTaskId(taskId);

	Task task = taskRepo.findById(taskId).orElse(null);
	if (task == null)
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Công việc này không tồn tại !");
	if (task.getTaskStatus().name().contains("CANCELLED"))
	    return ResponseEntity.status(HttpStatus.OK)
		    .body(" Lịch này đã bị hủy ! Bạn không thể báo cáo tiến trình cho công việc này !");
	if (taskReport.isEmpty())
	    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Hiện chưa có báo cáo cho tiến trình của công việc này !");

	List<TaskReport> taskReports = taskReportRepo.findAllByTaskId(task.getTaskId());

	if (!taskReport.isEmpty()) {
	    taskReports.forEach(x -> {
		List<Image> reportTaskImgs = imgRepo
			.findAllByEntityIdAndImageType(x.getTaskReportId(), ImageType.WORKING).orElse(List.of());
		x.setTaskReportImages(reportTaskImgs);
	    });
	}
	return ResponseEntity.ok().body(taskReport);
    }

 
}
