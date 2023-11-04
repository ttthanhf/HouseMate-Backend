package housemate.models;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import housemate.constants.Enum.TaskStatus;
import housemate.entities.Image;
import housemate.entities.Schedule;
import housemate.entities.Service;
import housemate.entities.ServiceFeedback;
import housemate.entities.ServiceType;
import housemate.entities.Staff;
import housemate.entities.TaskReport;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskViewDTO {

	private int taskId;
	
	private LocalDateTime createdAt;

	private TaskStatus taskStatus;
	
	private String addressWorking;
	
	@JsonInclude(content = Include.NON_NULL)
	private String taskNote;
	
	private LocalDateTime receivedAt;

	private Staff staff;
	
	private Schedule schedule;

	private CustomerViewOnTask customer;
	
	private ServiceViewOnTask service;

	private List<TaskReport> taskReportList ;
	
	private ServiceFeedbackViewOnTask feedback;
	
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
		private String packageName;
		private String unitOfMeasure;
		private String groupType;
		private boolean isPackage;
		private int min;
		private int max;
		private ServiceType serviceType;
		private List<Image> images;
		
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ServiceFeedbackViewOnTask {
		private int serviceFeedbackId;
		private int serviceId;
		private int taskId;
		private int customerId;
		private int rating;
		private String content;
		private LocalDateTime createdAt;
		

	}
	
}
