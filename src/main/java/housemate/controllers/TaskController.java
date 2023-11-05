package housemate.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import housemate.constants.Enum.TaskReportType;
import housemate.constants.Enum.TaskStatus;
import housemate.entities.Schedule;
import housemate.models.TaskReportNewDTO;
import housemate.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "*")
@Tag(name = "Task")
@RequestMapping("/tasks")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

	@Autowired
	TaskService taskServiceDao;
	
	@GetMapping("/tasks-pending-application")
	@Operation(summary = "Get all tasks are opening for application")
	public ResponseEntity<?> getAllTaskInPendingApplication(
			@RequestParam(required = false) Optional<Sort.Direction> directionSort,
			@RequestParam(required = false) Optional<Integer> page,
			@RequestParam(required = false) Optional<Integer> size) {
		return taskServiceDao.getAllTaskInPendingApplication(directionSort, page, size);
	}
	
	@GetMapping("/staff")
	@Operation(summary = "Filter the task status based on the current staff - Role: current STAFF only")
	public ResponseEntity<?> getAllTaskForStaffByTaskStatus(
			HttpServletRequest request,
			@RequestParam(required = false)TaskStatus taskStatus,
			@RequestParam(required = false) Optional<Integer> page,
			@RequestParam(required = false) Optional<Integer> size) {
		return taskServiceDao.getAllTaskForStaffByTaskStatus(request, taskStatus, page, size);
	}
	
	@GetMapping("/{task-id}")
	@Operation(summary = "View the specific task in details")
	public ResponseEntity<?> getTaskInDetails(@PathVariable("task-id") int taskId) {
		return taskServiceDao.getTaskInDetails(taskId);
	}
	
	@GetMapping("/schedule/{schedule-id}")
	@Operation(summary = "View the task in details of specific schedule - Role: current CUSTOMER + ADMIN")
	public ResponseEntity<?> getTaskViewInDetailsForCustomerByScheduleId(
			HttpServletRequest request,
			@PathVariable("schedule-id") int scheduleId) {
		return taskServiceDao.getTaskViewInDetailsForCustomerByScheduleId(request, scheduleId);
	}
	
	@PostMapping("/new/schedule/{schedule-id}")
	@Operation(summary = "View the task in details of specific schedule - Role: CUSTOMER - owner of the schedule")
	public ResponseEntity<?> createNewTask(
			HttpServletRequest request,
			@PathVariable("schedule-id") int scheduleId) {
		return taskServiceDao.createNewTask(request, scheduleId);
	}
	
	@DeleteMapping("/cancel/schedule/{schedule-id}")
	@Operation(summary = "Cancel the task of specific schedule - Role: CUSTOMER - owner of the schedule + STAFF - reponsible for doing the task of this schedule")
	public ResponseEntity<?> cancelTask(
			HttpServletRequest request,
			@PathVariable("schedule-id") int scheduleId) {
		return taskServiceDao.cancelTask(request, scheduleId);
	}
	
	@PutMapping("/new-time/schedule")
	@Operation(summary = "Update the task timeworking based on specific schedule - Role: CUSTOMER - owner of the schedule")
	public ResponseEntity<?> updateTaskTimeWorking(
			HttpServletRequest request,
			Schedule oldSchedule,
			Schedule newSchedule) {
		return taskServiceDao.updateTaskTimeWorking(request, oldSchedule, newSchedule);
	}
	
	@PostMapping("{task-id}/staff/application")
	@Operation(summary = "Apply for doing specific task - Role: STAFF only")
	public ResponseEntity<?> approveStaff(
			HttpServletRequest request,
			@PathVariable("task-id") int taskId) {
		return taskServiceDao.approveStaff(request, taskId);
	}

	@PostMapping("{task-id}/staff/reports")
	@Operation(summary = "Report task progression of specific task - Role: STAFF responsible for this task")
	public ResponseEntity<?> reportTaskByStaff(
			HttpServletRequest request,
			@PathVariable("id") int taskId,
			TaskReportType taskReportType,
			TaskReportNewDTO reportnewDTO) {
		return taskServiceDao.reportTaskByStaff(request, taskId, taskReportType, reportnewDTO);
	}
	
	@GetMapping("{task-id}/reports")
	@Operation(summary = "View task progressions of specific task")
	public ResponseEntity<?> getTaskReportListByTask(
			@PathVariable("task-id") int taskId) {
		return taskServiceDao.getTaskReportListByTask(taskId);
	}
	
	@GetMapping("/staffs")
	public ResponseEntity<?> getAllStaffs() {
		return taskServiceDao.getAllStaff();
	}
	
	
	
}
