/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.controllers;

import housemate.entities.ServiceComment;
import housemate.models.CommentDTO;
import housemate.services.CommentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
public class CommentController {

    @Autowired
    CommentService commentService;

    @GetMapping("/services/{serviceId}")
    public ResponseEntity<List<ServiceComment>> getAllCommentByServiceId(@Valid @PathVariable int serviceId) {
        return commentService.getAllCommentByServiceId(serviceId);
    }

    @PostMapping("/")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<String> addComment(HttpServletRequest request, @Valid @RequestBody CommentDTO.Add commentAdd) {
        return commentService.addComment(request, commentAdd);
    }

    @DeleteMapping("/{commentId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<String> removeComment(HttpServletRequest request, @Valid @PathVariable int commentId) {
        return commentService.removeComment(request, commentId);
    }
}
