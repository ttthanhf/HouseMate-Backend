/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import housemate.entities.Service;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import housemate.constants.Enum.SaleStatus;

/**
 *
 * @author ThanhF
 */
@Transactional
@Repository
public interface ServiceRepository extends JpaRepository<Service, Integer> {

	List<Service> findAll();

	// get all available services for customer
	@Query(value = "SELECT s FROM Service s WHERE s.saleStatus <> 'DISCONTINUED'")
	List<Service> findAllAvailable();

    @Query("SELECT s.finalPrice FROM Service s WHERE s.serviceId = :serviceId")
    int getFinalPriceByServiceId(@Param("serviceId") int serviceId);
	
	List<Service> findAllByIsPackageFalse();

	@Query("SELECT s FROM Service s WHERE "
			+ "(:saleStatus IS NULL OR s.saleStatus = :saleStatus) "
			+ "AND s.saleStatus <> 'DISCONTINUED'"
			+ "AND (:keyword IS NULL OR LOWER(s.titleName) LIKE LOWER(CONCAT('%', :keyword, '%')))  "
			+ "AND s.avgRating >= :ratingFrom "
			+ "AND (:isPackage IS NULL OR s.isPackage = :isPackage) ")
	Page<Service> searchFilterAllAvailable(
			@Param("saleStatus") SaleStatus saleStatus,
			@Param("keyword") String keyword,
			@Param("ratingFrom") int ratingFrom,
			@Param("isPackage") Boolean isPackage,
			Pageable page);
	
	@Query("SELECT s FROM Service s WHERE "
			+ "s.saleStatus = 'ONSALE' "
			+ "ORDER BY s.numberOfSold DESC "
			+ "LIMIT 4 ")
	List<Service> findTopSale();

	Optional<Service> findByServiceId(int id);

	Service findByTitleNameIgnoreCase(String titleName);
	
	@Transactional
	@Modifying
	@Query("UPDATE Service s SET s.avgRating = "
	        + "(SELECT COALESCE(AVG(f.rating), 0) FROM ServiceFeedback f WHERE f.serviceId = :serviceId) "
	        + "WHERE s.serviceId = :serviceId")
	void updateAvgRating(@Param("serviceId") int serviceId);
	
	@Query("SELECT s.originalPrice FROM Service s WHERE s.serviceId = :serviceId")
	int getOriginalPriceByServiceId(@Param("serviceId") int serviceId);

	@Query("SELECT s.salePrice FROM Service s WHERE s.serviceId = :serviceId")
	int getSalePriceByServiceId(@Param("serviceId") int serviceId);

	@Query("SELECT s FROM Service s WHERE s.serviceId = :serviceId")
	Service getServiceByServiceId(@Param("serviceId") int serviceId);

    @Modifying
    @Transactional
    @Query("UPDATE Service s SET s.numberOfSold = s.numberOfSold + :quantity WHERE s.serviceId = :serviceId")
    void updateNumberOfSoldByServiceId(@Param("serviceId") int serviceId, int quantity);

}
