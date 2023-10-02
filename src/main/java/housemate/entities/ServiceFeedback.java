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

<<<<<<< HEAD
    @ManyToOne
    @JoinColumn(name = "package_id", nullable = true, insertable = false, updatable = false)
    private PackageService packageService;

=======
    @Column(name = "package_id", columnDefinition = "integer default 0")
    private Integer packageId;
>>>>>>> 1af5ca64aeceb766f630c5431341dbd512787af3

    @Column(name = "customer_id")
    private int customerId;

    @Column(name = "content")
    private int content;

    @Column(name = "created_at")
    private int createdAt;

    @Column(name = "rating", columnDefinition = "float default 0")
    private Float rating;


    
}

