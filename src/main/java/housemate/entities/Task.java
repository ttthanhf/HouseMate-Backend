/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import housemate.constants.Enum.TaskStatus;

/**
 *
 * @author ThanhF
 */
@Entity
@Table(name = "task")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "task_id")
	private int taskId;

	@Column(name = "service_schedule_id")
	private int scheduleId;
	
	@Column(name = "parent_schedule_id")
	private int parentScheduleId;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "staff_id")
	private Integer staffId;

	@Column(name = "post_at")
	private LocalDateTime receivedAt;

	@Column(name = "task_status")
	@Enumerated(EnumType.STRING)
	private TaskStatus taskStatus;
	
	@Column(name = "task_note")
	private String taskNote;
	
	@OneToOne
	@JoinColumn(name = "service_schedule_id", referencedColumnName = "service_schedule_id", updatable = false, insertable = false)
	private Schedule schedule;
	
	@OneToOne
	@JoinColumn(name = "user_id", referencedColumnName = "staff_id", updatable = false, insertable = false)
	private Staff staff;

}
