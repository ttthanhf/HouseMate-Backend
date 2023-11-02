package housemate.models;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import housemate.constants.Enum.TaskStatus;
import housemate.entities.Image;
import housemate.entities.Schedule;
import housemate.entities.ServiceType;
import housemate.entities.Staff;
import housemate.entities.TaskReport;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskViewDTO {

	private int taskId;

	private Schedule schedule;
	
	private LocalDateTime createdAt;
	
	private CustomerViewOnTask customer;
	
	private ServiceViewOnTask service;
	
	private String addressWorking;

	private Staff staff;

	private LocalDateTime receivedAt;
	
	private TaskStatus taskStatus;
	
	private List<TaskReport> taskReportList ;
	
	@JsonInclude(content = Include.NON_NULL)
	private String taskNote;
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CustomerViewOnTask {
		private int userId;
		private String fullName;
		private String phoneNumber;
		private String emailAddress;
		private List<Image> avatar;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ServiceViewOnTask {
		private int serviceId;
		private String titleName;
		private String unitOfMeasure;
		private String description;
		private String groupType;
		private boolean isPackage;
		private int min;
		private int max;
		private List<Image> images;
		private ServiceType serviceType;
	}
	
}
