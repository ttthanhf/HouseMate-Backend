/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.repositories;

import java.util.List;
<<<<<<< HEAD
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import housemate.constants.Enum.SaleStatus;
import housemate.entities.Service;
=======
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.session.Session;
import org.springframework.stereotype.Repository;

import housemate.constants.SaleStatus;
import housemate.entities.Service;
import jakarta.persistence.QueryHint;
>>>>>>> 1af5ca64aeceb766f630c5431341dbd512787af3
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
	
<<<<<<< HEAD
	List<Service> findByAvgRatingGreaterThanEqual(int requiredRating);
	
=======
>>>>>>> 1af5ca64aeceb766f630c5431341dbd512787af3
	@Transactional
	@Modifying
	@Query(value = "UPDATE Service s SET s.avgRating = (SELECT AVG(f.rating) FROM ServiceFeedback f WHERE f.service = s)")
	void updateAvgRating();
	
	@Transactional
	@Modifying
	@Query(value = "UPDATE Service s SET s.numberOfSold = (SELECT COUNT(soi) FROM ServiceOrderItem soi WHERE soi.service = s)")

	void updatetheNumberOfSold();
	

	
	}
