/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.repositories;

import housemate.entities.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author ThanhF
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    @Query("SELECT n FROM Notification n WHERE n.userId = :userId")
    List<Notification> getAllNotificationByUserId(int userId);

    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.notificationId = :notificationId")
    Notification getNotificationByNotificationIdAndUserId(int notificationId, int userId);
}
