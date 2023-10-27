/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.repositories;

import housemate.entities.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author hdang09
 */
@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    List<Schedule> getByCustomerId(int customerId);

    Schedule getByScheduleId(int scheduleId);

    @Query("SELECT IFNULL(SUM(s.quantityRetrieve), 0) FROM Schedule s WHERE s.serviceId = :serviceId AND s.customerId = :customerId")
    int getSumOfQuantityRetrieve(@Param("serviceId") int serviceId, @Param("customerId") int customerId);
    
    @Query("SELECT s FROM Schedule s WHERE datediff(s.startDate, NOW()) BETWEEN 0 AND :duration")
    List<Schedule> findAllScheduleUpComing(@Param("duration") int duration);
}
