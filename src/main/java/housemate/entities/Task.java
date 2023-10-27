/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

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
	private int id;

	@Column(name = "service_schedule_id")
	private int serviceScheduleId;

	@Column(name = "created_at")
	private LocalDate created_at;

	@Column(name = "staff_id")
	private int staffId;

	@Column(name = "post_at")
	private LocalDate received_at;

	@Column(name = "task_status")
	private String taskStatus;

}
