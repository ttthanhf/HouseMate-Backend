/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.repositories;

import housemate.entities.Schedule;
import housemate.responses.ReportRes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    void cancelThisAndFollowingSchedule(@Param("scheduleId") int scheduleId, @Param("parentScheduleId") int parentScheduleId);

    @Transactional
    void deleteByScheduleIdGreaterThanEqualAndParentScheduleIdEquals(int scheduleId, int parentScheduleId);

    @Modifying
    @Transactional
    @Query("UPDATE Schedule s SET s.parentScheduleId = :scheduleId + 1 " +
            "WHERE s.scheduleId <> :scheduleId AND s.parentScheduleId = :scheduleId")
    void updateChildrenSchedule(@Param("scheduleId") int scheduleId);
    
    @Query("SELECT COUNT(s) FROM Schedule s WHERE s.customerId = :customerId")
    int countAllScheduleByUserId(@Param("customerId") int customerId);

    int countByCustomerId(int customerId);

    @Query("SELECT new housemate.responses.ReportRes(s.serviceId, '', COALESCE(SUM(s.quantityRetrieve), 0), '') FROM Schedule s " +
            "WHERE s.customerId = :customerId AND s.endDate >= :startDate AND s.endDate < :endDate AND s.status = 'DONE' " +
            "GROUP BY s.serviceId")
    List<ReportRes> getMonthlyReportForCustomer(int customerId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT new housemate.responses.ReportRes(s.serviceId, '', COALESCE(SUM(s.quantityRetrieve), 0), '') FROM Schedule s " +
            "WHERE s.staffId = :staffId AND s.endDate >= :startDate AND s.endDate < :endDate AND s.status = 'DONE' " +
            "GROUP BY s.serviceId")
    List<ReportRes> getMonthlyReportForStaff(int staffId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT new housemate.responses.ReportRes(s.serviceId, '', COALESCE(SUM(s.quantityRetrieve), 0), '') FROM Schedule s " +
            "WHERE s.staffId = :staffId AND s.status = 'DONE' GROUP BY s.serviceId")
    List<ReportRes> getStaffAchievement(int staffId);

    @Query("SELECT s FROM Schedule s WHERE s.customerId = :customerId AND s.status = 'DONE'")
    List<Schedule> getHistoryUsage(int customerId);
}
