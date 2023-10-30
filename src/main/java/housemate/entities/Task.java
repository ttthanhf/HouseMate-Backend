/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 *
 * @author ThanhF
 */
@Entity
@Table(name = "Task")
@Data
@NoArgsConstructor
@AllArgsConstructor
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

}
