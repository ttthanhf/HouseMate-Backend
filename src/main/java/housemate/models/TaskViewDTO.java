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
import lombok.Data;

@Data
public class TaskViewDTO {

	private int taskId;

	private Schedule shedule;
	
	private LocalDateTime createdAt;
	
	private CustomerViewOnTask customer;
	
	private ServiceViewOnTask service;
	
	private String addressWorking;

	private Staff staff;

	private LocalDateTime receivedAt;
	
	private TaskStatus taskStatus;
	
	@JsonInclude(content = Include.NON_NULL)
	private String taskNote;
	
	@Data
	public class CustomerViewOnTask {
		private int userId;
		private String fullName;
		private String phoneNumber;
		private String emailAddress;
		private List<Image> avatar;
	}
	
	@Data
	public class ServiceViewOnTask {
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
