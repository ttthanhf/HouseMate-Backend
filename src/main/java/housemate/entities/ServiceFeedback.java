/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.entities;

import jakarta.persistence.*;

/**
 *
 * @author ThanhF
 */
@Entity
@Table(name = "Service_Feedback")
public class ServiceFeedback {

    @Id
    @Column(name = "service_feedback_id")
    private int id;

    @Column(name = "task_id")
    private int taskId;

    @Column(name = "service_id")
    private int serviceId;

    @Column(name = "package_id")
    private int packageId;

    @Column(name = "customer_id")
    private int customerId;

    @Column(name = "content")
    private int content;

    @Column(name = "created_at")
    private int createdAt;

    @Column(name = "rating")
    private int rating;

    public ServiceFeedback(int id, int taskId, int serviceId, int packageId, int customerId, int content, int createdAt, int rating) {
        this.id = id;
        this.taskId = taskId;
        this.serviceId = serviceId;
        this.packageId = packageId;
        this.customerId = customerId;
        this.content = content;
        this.createdAt = createdAt;
        this.rating = rating;
    }

    
}

