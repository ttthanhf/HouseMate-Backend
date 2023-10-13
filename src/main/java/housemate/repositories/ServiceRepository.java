/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

	//get all services for admin
	List<Service> findAll(); 
	
	//get all available services for customer
	@Query( value = "SELECT s FROM Service s WHERE s.saleStatus <> 'DISCONTINUED'")
	List<Service> findAllAvailable(); 
	
	List<Service> findAllByIsPackageFalse();
	
	@Query("SELECT s FROM Service s WHERE "
			+ "s.saleStatus = :saleStatus " 
			+ "AND LOWER(s.titleName) LIKE LOWER(CONCAT('%', :keyword, '%')) "
			+ "AND s.avgRating >= :ratingFrom " 
			)
    List<Service> searchFilterAllKind(
    		@Param("saleStatus")SaleStatus saleStatus,
    		@Param("keyword")String keyword,
    		@Param("ratingFrom")int ratingFrom,
    		Sort sort
    		);
	
	@Query("SELECT s FROM Service s WHERE "
			+ "s.saleStatus = 'ONSALE' " 
			+ "ORDER BY s.numberOfSold DESC " 
			+ "LIMIT 4 "
			)
    List<Service> findTopSale();

	Optional<Service> findByServiceId(int id);
	
	Service findByTitleNameIgnoreCase(String titleName);
	
	@Transactional
	@Modifying
	@Query(value = "UPDATE Service s SET s.avgRating = "
				    + "(SELECT COALESCE(AVG(f.rating),0) FROM ServiceFeedback f WHERE f.service = s) "
			        + "WHERE NOT EXISTS (SELECT 1 FROM ServiceFeedback f WHERE f.service = s)"
				    )
	void updateAvgRating();
	
	@Transactional
	@Modifying
	@Query(value = "UPDATE Service s SET s.numberOfSold = "
					+ "(SELECT COALESCE(COUNT(soi),0) FROM ServiceOrderItem soi WHERE soi.service = s) "
					+ "WHERE NOT EXISTS (SELECT 1 FROM ServiceOrderItem soi WHERE soi.service = s)"
					)
	void updatetheNumberOfSold();
	
	}
