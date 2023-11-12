/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author ThanhF
 */
@Data
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private int notificationId;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "notification_created_at")
    private LocalDateTime notificationCreatedAt;

    @Column(name = "is_read")
    private boolean isRead;

    @Column(name = "message")
    private String message;

    @Column(name = "title")
    private String title;

    @Column(name = "entity_id")
    private int entityId;

}
