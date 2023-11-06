/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.repositories;

import housemate.entities.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    List<Schedule> getByStaffId(int staffId);

    @Query(value = "SELECT COALESCE(SUM(s.quantityRetrieve), 0) FROM Schedule s WHERE s.userUsageId = :userUsageId")
    int getTotalQuantityRetrieveByUserUsageId(@Param("userUsageId") int userUsageId);

    List<Schedule> getAllByParentScheduleId(int parentScheduleId);

    @Modifying
    @Query("DELETE FROM Schedule s " +
            "WHERE s.scheduleId >= :scheduleId AND s.parentScheduleId = (" +
            "    SELECT sc.parentScheduleId FROM Schedule sc WHERE sc.scheduleId = :scheduleId" +
            ")")
    void deleteThisAndFollowing(@Param("scheduleId") int scheduleId);

    @Modifying
    @Query("UPDATE Schedule s SET s.parentScheduleId = :scheduleId " +
            "WHERE s.scheduleId <> :scheduleId AND s.parentScheduleId = (" +
            "    SELECT sc.parentScheduleId FROM Schedule sc WHERE sc.scheduleId = :scheduleId" +
            ")")
    void updateChildrenSchedule(@Param("scheduleId") int scheduleId);
}
