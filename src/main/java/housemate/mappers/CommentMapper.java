/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.mappers;

import housemate.entities.ServiceComment;
import housemate.models.CommentDTO;
import org.springframework.stereotype.Component;

/**
 *
 * @author ThanhF
 */
@Component
public class CommentMapper {

    public ServiceComment mapToEntity(CommentDTO.Add commentDTO) {
        ServiceComment serviceComment = new ServiceComment();
        serviceComment.setServiceId(commentDTO.getServiceId());
        serviceComment.setUserId(commentDTO.getUserId());
        serviceComment.setText(commentDTO.getText());
        serviceComment.setDate(commentDTO.getDate());
        return serviceComment;
    }
}
