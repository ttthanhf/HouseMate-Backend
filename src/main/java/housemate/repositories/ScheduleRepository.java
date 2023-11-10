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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 * @author hdang09
 */
@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    List<Schedule> getByCustomerId(int customerId);

    List<Schedule> getByStaffId(int staffId);

    @Query(value = "SELECT COALESCE(SUM(s.quantityRetrieve), 0) FROM Schedule s WHERE s.userUsageId = :userUsageId")
    int getTotalQuantityRetrieveByUserUsageId(@Param("userUsageId") int userUsageId);

    @Transactional
    @Modifying
    @Query("UPDATE Schedule s SET s.status = 'CANCEL', s.quantityRetrieve = 0 WHERE s.scheduleId = :scheduleId")
    void cancelThisSchedule(@Param("scheduleId") int scheduleId);

    @Transactional
    @Modifying
    @Query("UPDATE Schedule s SET s.status = 'CANCEL', s.quantityRetrieve = 0 " +
            "WHERE s.scheduleId >= :scheduleId AND s.parentScheduleId = :parentScheduleId")
    void cancelThisAndFollowingSchedule(@Param("scheduleId") int scheduleId, @Param("scheduleId") int parentScheduleId);

    @Transactional
    void deleteByScheduleIdGreaterThanEqualAndParentScheduleIdEquals(int scheduleId, int parentScheduleId);

    @Modifying
    @Transactional
    @Query("UPDATE Schedule s SET s.parentScheduleId = :scheduleId + 1 " +
            "WHERE s.scheduleId <> :scheduleId AND s.parentScheduleId = :scheduleId")
    void updateChildrenSchedule(@Param("scheduleId") int scheduleId);

    int countByCustomerId(int customerId);
}
