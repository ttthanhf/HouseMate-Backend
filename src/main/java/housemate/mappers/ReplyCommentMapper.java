/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.mappers;

import housemate.entities.ReplyComment;
import housemate.models.ReplyCommentAddDTO;
import org.springframework.stereotype.Component;

/**
 *
 * @author ThanhF
 */
@Component
public class ReplyCommentMapper {

    public ReplyComment mapToEntity(ReplyCommentAddDTO replyCommentDTO) {
        ReplyComment replyComment = new ReplyComment();
        replyComment.setCommentId(replyCommentDTO.getCommentId());
        replyComment.setUserId(replyCommentDTO.getUserId());
        replyComment.setText(replyCommentDTO.getText());
        replyComment.setDate(replyCommentDTO.getDate());
        return replyComment;
    }
}
