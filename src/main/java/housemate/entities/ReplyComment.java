/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.entities;

import housemate.models.UserDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author ThanhF
 */
@Entity
@Table(name = "reply_comments")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ReplyComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_id")
    private int replyId;

    @Column(name = "comment_id")
    private int commentId;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "text")
    private String text;

    @Column(name = "date")
    private LocalDateTime date;

    @Transient
    private UserDTO userDetail;
}
