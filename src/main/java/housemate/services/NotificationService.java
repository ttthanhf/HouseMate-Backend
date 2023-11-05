package housemate.services;

import housemate.entities.Notification;
import housemate.models.NotificationDTO;
import housemate.repositories.NotificationRepository;
import housemate.utils.AuthorizationUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author ThanhF
 */
public class NotificationService {

    @Autowired
    AuthorizationUtil authorizationUtil;

    @Autowired
    NotificationRepository notificationRepository;

    public ResponseEntity<List<Notification>> getAllNotificationByUser(HttpServletRequest request) {

        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);

        List<Notification> listNotification = notificationRepository.getAllNotificationByUserId(userId);

        return ResponseEntity.status(HttpStatus.OK).body(listNotification);
    }

    public ResponseEntity<Notification> createNotification(HttpServletRequest request, NotificationDTO notificationDTO) {

        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setDatetime(notificationDTO.getDatetime());
        notification.setRead(false);
        notification.setServiceName(notificationDTO.getServiceName());
        notification.setNotificationCreatedAt(LocalDateTime.now());
        notification.setStatus(notificationDTO.getStatus());

        notificationRepository.save(notification);

        return ResponseEntity.status(HttpStatus.OK).body(notification);
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
