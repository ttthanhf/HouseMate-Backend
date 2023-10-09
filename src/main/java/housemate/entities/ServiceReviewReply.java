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
@Table(name = "Service_Review_Reply")
public class ServiceReviewReply {

    @Id
    @Column(name = "reply_id")
    private int id;

    @Column(name = "service_comment_id")
    private int serviceCommentId;

    @Column(name = "replier_id")
    private int replierId;

    @Column(name = "reply_text")
    private String replyText;

    @Column(name = "reply_date")
    private Date replyDate;

    public ServiceReviewReply(int id, int serviceCommentId, int replierId, String replyText, Date replyDate) {
        this.id = id;
        this.serviceCommentId = serviceCommentId;
        this.replierId = replierId;
        this.replyText = replyText;
        this.replyDate = replyDate;
    }

}
