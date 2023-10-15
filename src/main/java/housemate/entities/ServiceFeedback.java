/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.entities;


import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Anh
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "service_feedback")
public class ServiceFeedback {

    @Id
    @Column(name = "service_feedback_id")
    private int serviceFeedbackId;

    @Column(name = "task_id")
    private int taskId;
    
    @ManyToOne
    @JoinColumn(name = "service_id", insertable = false, updatable = false)
    private Service service;

    @Column(name = "service_id")
    private int serviceId;
    
    @Column(name = "customer_id")
    private int customerId;

    @Column(name = "content")
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "rating", columnDefinition = "float default 0")
    private Float rating;

}