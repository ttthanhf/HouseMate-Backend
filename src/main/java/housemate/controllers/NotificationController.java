package housemate.controllers;

import housemate.entities.Notification;
import housemate.services.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author ThanhF
 */
@RestController
@RequestMapping("/notifications")
@CrossOrigin
@Tag(name = "Notification")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    @Autowired
    NotificationService notificationService;

    @Operation(summary = "Get all notification for user")
    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotificationByUser(HttpServletRequest request) {
        return notificationService.getAllNotificationByUser(request);
    }

    @Operation(summary = "Change status read notification to true")
    @GetMapping("/{notificationId}/read")
    public ResponseEntity<String> updateReadStatusNotification(HttpServletRequest request, @PathVariable int notificationId) {
        return notificationService.updateReadStatusNotification(request, notificationId);
    }

    @Operation(summary = "Change all status read notification to true")
    @GetMapping("/read-all")
    public ResponseEntity<String> updateAllReadStatusNotification(HttpServletRequest request) {
        return notificationService.updateAllReadStatusNotification(request);
    }
}
