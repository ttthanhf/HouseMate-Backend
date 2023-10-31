/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.services;

import housemate.models.WebSocketDTO;
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

    public void sendNotificationToUser(String userId, WebSocketDTO webSocketDTO) {
        template.convertAndSendToUser(userId, "/queue/notification", webSocketDTO);
    }

    public void sendNotificationToEveryone(WebSocketDTO webSocketDTO) {
        template.convertAndSend("/topic/messages", webSocketDTO);
    }

    public void sendNotification(WebSocketDTO webSocketDTO) {
        String userId = webSocketDTO.getUserId();
        if (userId == null || userId.equals("")) {
            webSocketDTO.setType("New Task");
            template.convertAndSend("/topic/messages", webSocketDTO);
        } else {
            webSocketDTO.setType("Notification for customer");
            template.convertAndSendToUser(userId, "/queue/notification", webSocketDTO);
        }
    }

}
