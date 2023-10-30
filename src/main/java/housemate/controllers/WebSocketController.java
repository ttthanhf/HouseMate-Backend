/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.controllers;

import housemate.websocketMessages.Message;
import housemate.websocketMessages.Notification;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ThanhF
 */
@RestController
public class WebSocketController {

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public Message send(Message message) {
        return message;
    }

    @MessageMapping("/a")
    @SendTo("/topic/newTask")
    public Message newTask(Message message) {
        return message;
    }

    @MessageMapping("/notification")
    @SendTo("/topic/messages")
    public Notification userNotification(Notification notification) {
        return notification;
    }
}
