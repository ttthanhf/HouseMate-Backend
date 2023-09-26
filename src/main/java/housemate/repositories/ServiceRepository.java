/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import housemate.entities.Service;
import housemate.entities.enums.SaleStatus;
import jakarta.transaction.Transactional;

/**
 *
 * @author ThanhF
 */
@Transactional
public interface ServiceRepository extends JpaRepository<Service, Integer> {
	
	List<Service> findByTitleNameContaining(String keyword);
	
	Service findByTitleName(String titleName);
	
	Service findByTitleNameIgnoreCase(String titleName);
	
	List<Service> findBySaleStatus(String saleStatus);
	
	@Query("SELECT s FROM Service s ORDER BY :fieldName :requireOrder")
    List<Service> sortByOneField(@Param("fieldName") String fieldName, @Param("requireOrder") String requireOrder);
	
	//sort by multiple field 
}
