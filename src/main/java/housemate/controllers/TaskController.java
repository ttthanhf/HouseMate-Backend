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
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
@Tag(name = "Task")
@RequestMapping("/tasks")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

	@Autowired
	TaskService taskServiceDao;
	
	@GetMapping("/tasks-pending-application")
	public ResponseEntity<?> getAllTaskInPendingApplication(
			@RequestParam(required = false) Optional<Sort.Direction> directionSort,
			@RequestParam(required = false) Optional<Integer> page,
			@RequestParam(required = false) Optional<Integer> size) {
		return taskServiceDao.getAllTaskInPendingApplication(directionSort, page, size);
	}
	
	@GetMapping("/staff")
	public ResponseEntity<?> getaAllKindOfTaskForStaffByTaskStatus(
			HttpServletRequest request,
			TaskStatus taskStatus,
			@RequestParam(required = false) Optional<Integer> page,
			@RequestParam(required = false) Optional<Integer> size) {
		return taskServiceDao.getaAllKindOfTaskForStaffByTaskStatus(request, taskStatus, page, size);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getTaskInDetails(@PathVariable("id") int taskId) {
		return taskServiceDao.getTaskInDetails(taskId);
	}
	
	@GetMapping("/schedule/{id}")
	public ResponseEntity<?> getTaskInDetailsByScheduleForCustomerView(
			HttpServletRequest request,
			@PathVariable("id") int scheduleId) {
		return taskServiceDao.getTaskInDetailsByScheduleForCustomerView(request, scheduleId);
	}
	
	@PostMapping("/new/schedule/{id}")
	public ResponseEntity<?> createNewTask(
			HttpServletRequest request,
			@PathVariable("id") int scheduleId) {
		return taskServiceDao.createNewTask(request, scheduleId);
	}
	
	@DeleteMapping("/cancel/schedule/{id}")
	public ResponseEntity<?> cancelTask(
			HttpServletRequest request,
			@PathVariable("id") int scheduleId) {
		return taskServiceDao.cancelTask(request, scheduleId);
	}
	
	@PutMapping("/new-time/schedule")
	public ResponseEntity<?> cancelTask(
			HttpServletRequest request,
			Schedule oldSchedule,
			Schedule newSchedule) {
		return taskServiceDao.updateTaskTimeWorking(request, oldSchedule, newSchedule);
	}
	
	@PostMapping("{id}/staff/application")
	public ResponseEntity<?> approveStaff(
			HttpServletRequest request,
			@PathVariable("id") int taskId) {
		return taskServiceDao.approveStaff(request, taskId);
	}

	@PostMapping("{id}/staff/report")
	public ResponseEntity<?> reportTaskByStaff(
			HttpServletRequest request,
			@PathVariable("id") int taskId,
			TaskReportType taskReportType,
			TaskReportNewDTO reportnewDTO) {
		return taskServiceDao.reportTaskByStaff(request, taskId, taskReportType, reportnewDTO);
	}
	
	@PostMapping("{id}/report")
	public ResponseEntity<?> reportTaskByStaff(
			@PathVariable("id") int taskId) {
		return taskServiceDao.getTaskReportListByTask(taskId);
	}
	
	
	
}
