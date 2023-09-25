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
@Table(name = "Task")
public class Task {

    @Id
    @Column(name = "task_id")
    private int id;

    @Column(name = "service_schedule_id")
    private int serviceScheduleId;

    @Column(name = "customer_id")
    private int customerId;

    @Column(name = "post_at")
    private Date postAt;

    @Column(name = "task_status")
    private String taskStatus;

    public Task(int id, int serviceScheduleId, int customerId, Date postAt, String taskStatus) {
        this.id = id;
        this.serviceScheduleId = serviceScheduleId;
        this.customerId = customerId;
        this.postAt = postAt;
        this.taskStatus = taskStatus;
    }
}
