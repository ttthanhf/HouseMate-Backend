/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.repositories;

import housemate.entities.UserUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author ThanhF
 */
@Repository
public interface UserUsageRepository extends JpaRepository<UserUsage, Integer> {
    List<UserUsage> getByUserId(int userId);

    @Query("SELECT u FROM UserUsage u " +
            "WHERE u.serviceId = :serviceId AND u.userId = :userId AND u.remaining <> 0 AND CURDATE() < u.endDate " +
            "ORDER BY u.endDate LIMIT 1")
    UserUsage getSoonerSchedule(int serviceId, int userId);
}
