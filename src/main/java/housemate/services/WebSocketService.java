/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.services;

import housemate.entities.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 *
 * @author ThanhF
 */
@Service
public class WebSocketService {

    @Autowired
    private SimpMessagingTemplate template;

    public void sendNotificationToUser(String userId, Notification notification) {
        template.convertAndSendToUser(userId, "/queue/notification", notification);
    }

    public void sendNotificationToEveryone(Notification notification) {
        template.convertAndSend("/topic/messages", notification);
    }

}
