package housemate.services;

import housemate.constants.Role;
import housemate.entities.Notification;
import housemate.entities.UserAccount;
import housemate.repositories.NotificationRepository;
import housemate.repositories.UserRepository;
import housemate.utils.AuthorizationUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author ThanhF
 */
@Service
public class NotificationService {
    
    @Autowired
    private AuthorizationUtil authorizationUtil;
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private WebSocketService webSocketService;
    
    public ResponseEntity<List<Notification>> getAllNotificationByUser(HttpServletRequest request) {
        
        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        
        List<Notification> listNotification = notificationRepository.getAllNotificationByUserId(userId);
        
        return ResponseEntity.status(HttpStatus.OK).body(listNotification);
    }
    
    public void createNotification(String userId, String message, String title, int entityId) {
        
        if (userId.equals(Role.STAFF.toString())) {
            List<UserAccount> listStaff = userRepository.findByRole(Role.STAFF);
            for (UserAccount staff : listStaff) {
                
                int aUserId = staff.getUserId();
                
                Notification notification = new Notification();
                notification.setUserId(aUserId);
                notification.setMessage(message);
                notification.setTitle(title);
                notification.setEntityId(entityId);
                notification.setRead(false);
                notification.setCreatedAt(LocalDateTime.now());
                notification = notificationRepository.save(notification);
                
                notification.setUser(staff);
                
                webSocketService.sendNotificationToUser(String.valueOf(aUserId), notification);
                
            }
        } else {
            int aUserId = Integer.parseInt(userId);
            Notification notification = new Notification();
            notification.setUserId(aUserId);
            notification.setMessage(message);
            notification.setTitle(title);
            notification.setEntityId(entityId);
            notification.setRead(false);
            notification.setCreatedAt(LocalDateTime.now());
            notification = notificationRepository.save(notification);
            
            notificationRepository.save(notification);
            
            UserAccount user = userRepository.findByUserId(aUserId);
            notification.setUser(user);
            
            webSocketService.sendNotificationToUser(userId, notification);
        }
        
    }
    
    public ResponseEntity<String> updateReadStatusNotification(HttpServletRequest request, int notificationId) {
        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        Notification notification = notificationRepository.getNotificationByNotificationIdAndUserId(userId, notificationId);
        if (notification == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notification not found");
        }
        notification.setRead(true);
        notificationRepository.save(notification);
        return ResponseEntity.status(HttpStatus.OK).body("Update read status notification success !");
    }
}
