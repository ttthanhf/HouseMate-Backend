/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.repositories;

import housemate.entities.UserUsage;
import java.util.List;

import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author ThanhF
 */
@Repository
public interface UserUsageRepository extends JpaRepository<UserUsage, Integer> {

    List<UserUsage> getByUserId(int userId);

    @Query("SELECT u FROM UserUsage u WHERE u.userId = :userId")
    List<UserUsage> getAllUserUsageByUserId(int userId);

    @Query("SELECT u FROM UserUsage u WHERE u.userId = :userId AND u.isExpired = false")
    List<UserUsage> getAllUserUsageByUserIdAndNotExpired(int userId);

    @Query("SELECT u FROM UserUsage u WHERE u.userId = :userId AND u.serviceId = :serviceId")
    List<UserUsage> getAllUserUsageByServiceIdAndUserId(int serviceId, int userId);

    @Query("SELECT u FROM UserUsage u WHERE u.userId = :userId AND u.orderItemId = :orderItemId AND u.isExpired = false")
    List<UserUsage> getAllUserUsageByOrderItemIdAndUserIdAndNotExpired(int orderItemId, int userId);

    @Query("SELECT u FROM UserUsage u "
            + "WHERE u.serviceId = :serviceId AND u.userId = :userId AND u.remaining <> 0 AND CURDATE() < u.endDate "
            + "ORDER BY u.endDate LIMIT 1")
    Optional<UserUsage> getSoonerSchedule(int serviceId, int userId);

    List<UserUsage> getAllByServiceIdAndUserId(int serviceId, int userId);
}
