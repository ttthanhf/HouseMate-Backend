package housemate.repositories;

import housemate.entities.Period;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author ThanhF
 */
public interface PeriodRepository extends JpaRepository<Period, Integer> {

    List<Period> findAllByServiceId(int serviceId);

    void deleteAllByServiceId(int serviceId);

    @Query("SELECT p FROM Period p WHERE p.periodId = :periodId")
    Period getPeriodById(@Param("periodId") int periodId);

    @Query("SELECT p FROM Period p WHERE p.periodId = :periodId AND p.serviceId = :serviceId")
    Period getPeriodByPeriodIdAndServiceId(@Param("periodId") int periodId, @Param("serviceId") int serviceId);

    @Query("SELECT p FROM Period p WHERE p.serviceId = :serviceId ORDER BY periodValue LIMIT 1")
    Period getPeriodByServiceIdAndGetFirstPeriodWithPeriodValue(int serviceId);

    @Query("SELECT p FROM Period p WHERE p.serviceId = :serviceId")
    List<Period> getAllPeriodByServiceId(int serviceId);
}
