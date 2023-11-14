/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.controllers;

import housemate.entities.Notification;
import housemate.services.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ThanhF
 */
@RestController
public class WebSocketController {

    @Autowired
    private WebSocketService webSocketService;

    @PostMapping("/ws/customer/{userId}")
    public void sendNotificationToUser(@PathVariable String userId, @RequestBody Notification notification) {
        webSocketService.sendNotificationToUser(userId, notification);
    }

    @PostMapping("/ws/staff")
    public void sendNotification(@RequestBody Notification notification) {
        webSocketService.sendNotificationToEveryone(notification);
    }
}
