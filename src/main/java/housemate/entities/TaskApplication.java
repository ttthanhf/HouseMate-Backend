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
@Table(name = "Task_Application")
public class TaskApplication {

    @Id
    @Column(name = "task_id")
    private int taskId;

    @Column(name = "staff_id")
    private Integer staffId;

    @Column(name = "is_assigned")
    private boolean isAssigned;

    @Column(name = "applied_at")
    private Date appliedAt;

    @Column(name = "received_at")
    private Date receivedAt;

    @Column(name = "task_status")
    private String taskStatus;

    public TaskApplication(int taskId, int staffId, boolean isAssigned, Date appliedAt, Date receivedAt, String taskStatus) {
        this.taskId = taskId;
        this.staffId = staffId;
        this.isAssigned = isAssigned;
        this.appliedAt = appliedAt;
        this.receivedAt = receivedAt;
        this.taskStatus = taskStatus;
    }

}
