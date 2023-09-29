/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.services;

import housemate.entities.ServiceComment;
import housemate.mappers.CommentMapper;
import housemate.models.CommentDTO;
import housemate.repositories.CommentRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 *
 * @author ThanhF
 */
@Service
public class CommentService {

    @Autowired
    CommentRepository commentRepository;

    public ResponseEntity<List<ServiceComment>> getAllCommentByServiceId(int serviceId) {
        List<ServiceComment> listComment = commentRepository.getAllCommentByServiceId(serviceId);
        return ResponseEntity.status(HttpStatus.OK).body(listComment);
    }

    public ResponseEntity<ServiceComment> addComment(CommentDTO.Add commentAdd) {
        CommentMapper commentMapper = new CommentMapper();
        ServiceComment serviceComment = commentMapper.mapDTOtoEntity(commentAdd);
        commentRepository.save(serviceComment);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(serviceComment);
    }

    public ResponseEntity<Void> removeComment(CommentDTO.Remove commentRemove) {
        CommentMapper commentMapper = new CommentMapper();
        ServiceComment serviceComment = commentMapper.mapDTOtoEntity(commentRemove);
        commentRepository.deleteById(serviceComment.getId());
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
