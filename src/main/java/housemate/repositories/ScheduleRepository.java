/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.repositories;

import housemate.constants.ScheduleStatus;
import housemate.entities.Schedule;
import housemate.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

    @Query("SELECT s FROM Schedule s WHERE " + "datediff(s.startDate, NOW()) BETWEEN 0 AND :duration "
	    + "AND s.status = :status")
    List<Schedule> findAllScheduleInUpComing(@Param("status") ScheduleStatus status, @Param("duration") int duration);

    @Query("SELECT s FROM Schedule s WHERE " + "datediff(s.startDate, NOW()) BETWEEN 0 AND :duration "
	    + "AND s.status = :status " + "AND s.parentScheduleId = :parentScheduleId ")
    List<Schedule> findAllByParentScheduleAndInUpComing(@Param("status") ScheduleStatus status,
	    @Param("duration") int duration, @Param("parentScheduleId") int parentScheduleId);

    @Query(value = "SELECT COALESCE(SUM(s.quantityRetrieve), 0) FROM Schedule s WHERE s.userUsageId = :userUsageId")
    int getTotalQuantityRetrieveByUserUsageId(@Param("userUsageId") int userUsageId);

    @Query("SELECT s FROM Schedule s WHERE s.staffId = :staffId AND (s.startDate <= :endDate AND s.endDate >= :startDate) AND s.status NOT LIKE '%CANCEL%'")
    List<Schedule> findByStaffIdAndStartDate(@Param("staffId") int staffId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    List<Schedule> getByStaffId(int staffId);

}
