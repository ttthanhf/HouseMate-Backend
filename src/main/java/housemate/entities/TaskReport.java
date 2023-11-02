/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import housemate.constants.Enum.TaskStatus;

/**
 *
 * @author ThanhF
 */
@Entity
@Table(name = "Task_Report")
@Data
public class TaskReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_report_id")
    private int taskReportId;

    @Column(name = "task_id")
    private int taskId;

    @Column(name = "task_status")
    private TaskStatus taskStatus;

    @Column(name = "report_at")
    private LocalDateTime reportAt;

    @Column(name = "note", length = 3000)
    private String note;
    
	@ManyToOne
	@JoinColumn(name = "task_id", referencedColumnName = "task_id", updatable = false, insertable = false)
	private Task task;
	
	@Transient
	List<Image> taskReportImages ;


    
}

