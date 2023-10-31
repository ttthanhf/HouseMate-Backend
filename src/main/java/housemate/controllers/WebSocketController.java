/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.controllers;

import housemate.models.WebSocketDTO;
import housemate.services.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
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

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public String send(String message) {
        return message;
    }

    @PostMapping("/ws/addNewTask")
    public void sendNotificationForEveryone(@RequestBody WebSocketDTO webSocketDTO) {
        webSocketService.sendNotificationToEveryone(webSocketDTO);
    }

    @PostMapping("/ws/customer")
    public void sendNotificationToUser(@RequestBody WebSocketDTO webSocketDTO) {
        webSocketService.sendNotificationToUser("usertest", webSocketDTO);
    }

    @PostMapping("/ws/send")
    public void sendNotification(@RequestBody WebSocketDTO webSocketDTO) {
        webSocketService.sendNotification(webSocketDTO);
    }

}
