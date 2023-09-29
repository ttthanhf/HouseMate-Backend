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
@Table(name = "Notification_Object")
public class NotificationObject {

    @Id
    @Column(name = "notification_object_id")
    private int id;

    @Column(name = "actor_id")
    private int actorId;

    @Column(name = "entity_id")
    private int entityId;

    @Column(name = "entity_type_id")
    private int entityTypeId;

    @Column(name = "title")
    private String title;

    @Column(name = "message", length = 3000)
    private String message;

    @Column(name = "link", length = 500)
    private String link;

    @Column(name = "create_at")
    private Date createAt;

    public NotificationObject(int id, int actorId, int entityId, int entityTypeId, String title, String message, String link, Date createAt) {
        this.id = id;
        this.actorId = actorId;
        this.entityId = entityId;
        this.entityTypeId = entityTypeId;
        this.title = title;
        this.message = message;
        this.link = link;
        this.createAt = createAt;
    }

    
}

