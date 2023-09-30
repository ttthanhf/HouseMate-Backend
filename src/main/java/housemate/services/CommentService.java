/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.services;

import housemate.entities.ServiceComment;
import housemate.mappers.CommentMapper;
import housemate.mappers.JwtPayloadMapper;
import housemate.models.CommentDTO;
import housemate.repositories.CommentRepository;
import housemate.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private JwtUtil jwtUtil;

    public ResponseEntity<List<ServiceComment>> getAllCommentByServiceId(int serviceId) {
        List<ServiceComment> listComment = commentRepository.getAllCommentByServiceId(serviceId);
        return ResponseEntity.status(HttpStatus.OK).body(listComment);
    }

    public ResponseEntity<String> addComment(HttpServletRequest request, CommentDTO.Add commentAdd) {
        if (request.getHeader("Authorization") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token empty");
        }
        String token = request.getHeader("Authorization").substring(7);
        Map<String, Object> payloadMap = jwtUtil.extractClaim(token, claims -> claims.get("payload", Map.class));
        if (!jwtUtil.isTokenValid(token, new JwtPayloadMapper().mapFromMap(payloadMap))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expired");
        }
        commentAdd.setUserId((int) payloadMap.get("id"));
        CommentMapper commentMapper = new CommentMapper();
        ServiceComment serviceComment = commentMapper.mapDTOtoEntity(commentAdd);
        commentRepository.save(serviceComment);
        return ResponseEntity.status(HttpStatus.CREATED).body("Comment created");
    }

    @Transactional
    public ResponseEntity<String> removeComment(HttpServletRequest request, CommentDTO.Remove commentRemove) {
        if (request.getHeader("Authorization") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token empty");
        }
        String token = request.getHeader("Authorization").substring(7);
        Map<String, Object> payloadMap = jwtUtil.extractClaim(token, claims -> claims.get("payload", Map.class));
        if (!jwtUtil.isTokenValid(token, new JwtPayloadMapper().mapFromMap(payloadMap))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expired");
        }
        commentRemove.setUserId((int) payloadMap.get("id"));
        CommentMapper commentMapper = new CommentMapper();
        ServiceComment serviceComment = commentMapper.mapDTOtoEntity(commentRemove);
        if (commentRepository.deleteComment(serviceComment.getId(), serviceComment.getUserId()) == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Comment not found");
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Comment removed");
    }
}
