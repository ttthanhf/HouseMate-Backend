/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import housemate.constants.Enum.SaleStatus;
import housemate.entities.Service;
import jakarta.transaction.Transactional;

/**
 *
 * @author ThanhF
 */
@Transactional
@Repository
public interface ServiceRepository extends JpaRepository<Service, Integer> {
	
	List<Service> findByTitleNameContaining(String keyword);
	
	Service findByTitleName(String titleName);
	
	Service findByTitleNameIgnoreCase(String titleName);
	
	List<Service> findBySaleStatus(SaleStatus saleStatus);
	
	List<Service> findByAvgRatingGreaterThanEqual(int requiredRating);
	
	@Transactional
	@Modifying
	@Query(value = "UPDATE Service s SET s.avgRating = (SELECT AVG(f.rating) FROM ServiceFeedback f WHERE f.service = s)")
	void updateAvgRating();
	
	@Transactional
	@Modifying
	@Query(value = "UPDATE Service s SET s.numberOfSold = (SELECT COUNT(soi) FROM ServiceOrderItem soi WHERE soi.service = s)")

	void updatetheNumberOfSold();
	

	
	}