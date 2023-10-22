package housemate.repositories;

import housemate.entities.Period;
import jakarta.transaction.Transactional;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
	
	Period findByServiceIdAndPeriodValue(int serviceId, int cycleValue);
	
	void deleteAllByServiceId(int serviceId);

    @Query("SELECT p FROM Period p WHERE p.periodId = :periodId")
    Period getPeriodByid(@Param("periodId") int periodId);

    @Modifying
    @Transactional
    @Query("UPDATE Period p SET p.percent = :percent WHERE p.periodId = :periodId")
    void updatePeriodPercentByPeriodId(@Param("periodId") int periodId, @Param("percent") Float percent);
    
}
