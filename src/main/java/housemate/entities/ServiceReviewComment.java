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
@Table(name = "Service_Review_Comment")
public class ServiceReviewComment {

    @Id
    @Column(name = "service_comment_id")
    private int id;

    @Column(name = "service_id")
    private int serviceId;

    @Column(name = "package_id")
    private int packageId;

    @Column(name = "commenter_id")
    private int commenterId;

    @Column(name = "context")
    private String context;

    @Column(name = "comment_date")
    private Date commentDate;

    public ServiceReviewComment(int id, int serviceId, int packageId, int commenterId, String context, Date commentDate) {
        this.id = id;
        this.serviceId = serviceId;
        this.packageId = packageId;
        this.commenterId = commenterId;
        this.context = context;
        this.commentDate = commentDate;
    }
}
