/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.controllers;

import housemate.entities.Comment;
import housemate.models.CommentAddDTO;
import housemate.models.ReplyCommentAddDTO;
import housemate.services.CommentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ThanhF
 */
@RestController
@RequestMapping("/comment")
@CrossOrigin
@Tag(name = "Comment")
public class CommentController {

    @Autowired
    CommentService commentService;

    @GetMapping("/services/{serviceId}")
    public ResponseEntity<List<Comment>> getAllCommentByServiceId(@Valid @PathVariable int serviceId) {
        return commentService.getAllCommentByServiceId(serviceId);
    }

    @PostMapping("/add")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<String> addComment(HttpServletRequest request, @Valid @RequestBody CommentAddDTO commentAdd) {
        return commentService.addComment(request, commentAdd);
    }

    @DeleteMapping("/remove/{commentId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<String> removeComment(HttpServletRequest request, @Valid @PathVariable int commentId) {
        return commentService.removeComment(request, commentId);
    }

    @PostMapping("/reply/add")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<String> addReplyComment(HttpServletRequest request, @Valid @RequestBody ReplyCommentAddDTO replyCommentAdd) {
        return commentService.addReplyComment(request, replyCommentAdd);
    }

    @DeleteMapping("/reply/remove/{replyCommentId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<String> removeReplyComment(HttpServletRequest request, @Valid @PathVariable int replyCommentId) {
        return commentService.removeReplyComment(request, replyCommentId);
    }
}
