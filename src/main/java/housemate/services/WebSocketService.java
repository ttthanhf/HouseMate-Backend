/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.services;

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

    private String brokerPoint = "/queue/messages";

    public void sendNotificationForUser(String userId, String message) {
        template.convertAndSendToUser(userId, brokerPoint, message);
    }
}
