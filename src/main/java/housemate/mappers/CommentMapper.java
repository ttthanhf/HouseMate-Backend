/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.mappers;

import housemate.entities.Comment;
import housemate.models.CommentAddDTO;
import org.springframework.stereotype.Component;

/**
 *
 * @author ThanhF
 */
@Component
public class CommentMapper {

    public Comment mapToEntity(CommentAddDTO commentDTO) {
        Comment serviceComment = new Comment();
        serviceComment.setServiceId(commentDTO.getServiceId());
        serviceComment.setUserId(commentDTO.getUserId());
        serviceComment.setText(commentDTO.getText());
        serviceComment.setDate(commentDTO.getDate());
        return serviceComment;
    }
}
