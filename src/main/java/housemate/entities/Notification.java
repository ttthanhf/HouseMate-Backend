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
@Table(name = "Notification")
public class Notification {

    @Id
    @Column(name = "notification_object_id")
    private int notificationObjectId;

    @Id
    @Column(name = "receiver_id")
    private int receiverId;

    @Column(name = "sent_at")
    private Date sentAt;

    @Column(name = "is_read")
    private boolean isRead;

    public Notification(int notificationObjectId, int receiverId, Date sentAt, boolean isRead) {
        this.notificationObjectId = notificationObjectId;
        this.receiverId = receiverId;
        this.sentAt = sentAt;
        this.isRead = isRead;
    }

    
}

