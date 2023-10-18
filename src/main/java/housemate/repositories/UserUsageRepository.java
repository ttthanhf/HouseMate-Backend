/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.repositories;

import housemate.entities.UserUsage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author ThanhF
 */
@Repository
public interface UserUsageRepository extends JpaRepository<UserUsage, Integer> {

    @Query("SELECT u FROM UserUsage u WHERE u.userId = :userId")
    List<UserUsage> getAllUserUsageByUserId(int userId);

    @Query("SELECT u FROM UserUsage u WHERE u.userId = :userId AND u.serviceId = :serviceId")
    List<UserUsage> getAllUserUsageByServiceIdAndUserId(int serviceId, int userId);
}
