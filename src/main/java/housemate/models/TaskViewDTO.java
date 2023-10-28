package housemate.models;

import java.time.LocalDate;

import housemate.constants.Enum.TaskStatus;
import housemate.entities.Schedule;
import housemate.entities.Staff;
import jakarta.persistence.Column;
import lombok.Data;

@Data
public class TaskViewDTO {

	private int taskId;

	private Schedule shedule;

	private LocalDate createdAt;

	private Staff staff;

	@Column(name = "post_at")
	private LocalDate received_at;

	private TaskStatus taskStatus;
}
