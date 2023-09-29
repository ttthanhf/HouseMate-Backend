/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.entities;

import jakarta.persistence.*;
import java.util.Date;

/**
 *
 * @author ThanhF
 */
@Entity
@Table(name = "Task_Report")
public class TaskReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_report_id")
    private int id;

    @Column(name = "task_id")
    private int taskId;

    @Column(name = "task_status")
    private String taskStatus;

    @Column(name = "report_at")
    private Date reportAt;

    @Column(name = "note", length = 3000)
    private String note;

    public TaskReport(int id, int taskId, String taskStatus, Date reportAt, String note) {
        this.id = id;
        this.taskId = taskId;
        this.taskStatus = taskStatus;
        this.reportAt = reportAt;
        this.note = note;
    }

    
}

