/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.repositories;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import housemate.entities.Service;
import housemate.entities.enums.SaleStatus;
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
	
//	//@Query("SELECT s FROM Service s ORDER BY :fieldName :requireOrder")
//    //List<Service> sortByOneField(@Param("fieldName") String fieldName, @Param("requireOrder") String requireOrder);
//    List<Service> sortByOneField( String fieldName, String requireOrder);
//	
	//sort by multiple field 
}
