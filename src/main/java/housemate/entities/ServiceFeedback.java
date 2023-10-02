/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author ThanhF
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


    @ManyToOne
    @JoinColumn(name = "package_id", nullable = true, insertable = false, updatable = false)
    private PackageService packageService;



    @Column(name = "customer_id")
    private int customerId;

    @Column(name = "content")
    private int content;

    @Column(name = "created_at")
    private int createdAt;

    @Column(name = "rating", columnDefinition = "float default 0")
    private Float rating;


    
}

