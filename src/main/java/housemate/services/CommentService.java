/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.services;

import housemate.constants.Role;
import housemate.entities.Comment;
import housemate.entities.ReplyComment;
import housemate.entities.UserAccount;
import housemate.mappers.CommentMapper;
import housemate.mappers.ReplyCommentMapper;
import housemate.mappers.UserMapper;
import housemate.models.CommentAddDTO;
import housemate.models.ReplyCommentAddDTO;
import housemate.repositories.CommentRepository;
import housemate.repositories.ReplyCommentRepository;
import housemate.repositories.UserRepository;
import housemate.utils.AuthorizationUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
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
    private CommentRepository commentRepository;

    @Autowired
    private ReplyCommentRepository replyCommentRepository;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private ReplyCommentMapper replyCommentMapper;

    @Autowired
    private AuthorizationUtil authorizationUtil;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<List<Comment>> getAllCommentByServiceId(int serviceId) {

        List<Comment> listComment = commentRepository.getAllCommentByServiceId(serviceId);

        //add user detail and reply comment for each comment
        for (Comment comment : listComment) {
            comment.setListReplyComment(getAllReplyCommentByCommentId(comment.getCommentId()));
            UserAccount userEntity = userRepository.findByUserId(comment.getUserId());
            comment.setUserDetail(userMapper.mapToDto(userEntity));
        }

        return ResponseEntity.status(HttpStatus.OK).body(listComment);
    }

    public ResponseEntity<String> addComment(HttpServletRequest request, CommentAddDTO commentAdd) {

        commentAdd.setUserId(authorizationUtil.getUserIdFromAuthorizationHeader(request));
        commentAdd.setDate(LocalDateTime.now());

        Comment serviceComment = commentMapper.mapToEntity(commentAdd);
        commentRepository.save(serviceComment);

        return ResponseEntity.status(HttpStatus.CREATED).body("Comment created");
    }

    @Transactional
    public ResponseEntity<String> removeComment(HttpServletRequest request, int commentId) {

        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        Role userRole = Role.valueOf(authorizationUtil.getRoleFromAuthorizationHeader(request));

        // if role admin => can remove comment 
        if (userRole.equals(Role.ADMIN) && commentRepository.deleteCommentByAdmin(commentId) != 0) {

            //remove all reply have parent commentId
            replyCommentRepository.deleteReplyCommentByCommentId(commentId);

            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Comment removed with admin role");
        }

        //if other role => can remove comment with userId
        if (commentRepository.deleteCommentByUser(commentId, userId) != 0) {

            //remove all reply have parent commentId
            replyCommentRepository.deleteReplyCommentByCommentId(commentId);

            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Comment removed");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Comment not found");
    }

    //Reply Comment Service
    private List<ReplyComment> getAllReplyCommentByCommentId(int commentId) {

        List<ReplyComment> listReplyComment = replyCommentRepository.getAllReplyCommentByCommentId(commentId);

        //add user Detail to reply comment
        for (ReplyComment replyComment : listReplyComment) {
            UserAccount userEntity = userRepository.findByUserId(replyComment.getUserId());
            replyComment.setUserDetail(userMapper.mapToDto(userEntity));
        }

        return listReplyComment;
    }

    public ResponseEntity<String> addReplyComment(HttpServletRequest request, ReplyCommentAddDTO replyCommentAdd) {

        replyCommentAdd.setUserId(authorizationUtil.getUserIdFromAuthorizationHeader(request));
        replyCommentAdd.setDate(LocalDateTime.now());

        //If comment exist => can reply to that comment
        Comment comment = commentRepository.getCommentById(replyCommentAdd.getCommentId());
        if (comment != null) {

            ReplyComment replyComment = replyCommentMapper.mapToEntity(replyCommentAdd);
            replyCommentRepository.save(replyComment);

            return ResponseEntity.status(HttpStatus.CREATED).body("Reply comment created");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("Reply comment can not create because comment not found");
    }

    @Transactional
    public ResponseEntity<String> removeReplyComment(HttpServletRequest request, int commentId) {

        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);

        if (replyCommentRepository.deleteReplyComment(commentId, userId) != 0) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Reply comment removed");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Reply comment not found");

    }

}
