package housemate.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import housemate.constants.Enum.SaleStatus;
import housemate.entities.PackageService;
import housemate.entities.Service;
import jakarta.transaction.Transactional;

@Repository
public interface PackageServiceRepository extends JpaRepository<PackageService, Integer> {
List<PackageService> findByTitleNameContaining(String keyword);
	
	Service findByTitleName(String titleName);
	
	Service findByTitleNameIgnoreCase(String titleName);
	
	List<PackageService> findBySaleStatus(SaleStatus saleStatus);
	
	List<PackageService> findByAvgRatingGreaterThanEqual(int requiredRating);
	
	//findAllBySummary
	List<PackageService.Summary> findAllBy();
		
	@Transactional
	@Modifying
	@Query(value = "UPDATE PackageService ps SET ps.avgRating = (SELECT AVG(f.rating) FROM ServiceFeedback f WHERE f.packageService = ps)")
	void updateAvgRating();
	
	@Transactional
	@Modifying
	@Query(value = "UPDATE PackageService ps SET ps.numberOfSold = (SELECT COUNT(soi) FROM ServiceOrderItem soi WHERE soi.packageService = ps)")
	void updateTheNumberOfSold();
	
	@Transactional
	@Modifying
	@Query(value = "UPDATE PackageService ps SET ps.originalPrice = (SELECT SUM(psi.usageLimit * s.originalPrice) FROM PackageServiceItem psi JOIN"
					+ " psi.service s WHERE psi.packageService = ps)")
	void updateTheOriginalPrice();
	
	@Transactional
	@Modifying
	@Query(value = "UPDATE PackageService ps SET ps.originalPrice = (SELECT SUM(psi.usageLimit * s.originalPrice) FROM PackageServiceItem psi JOIN"
					+ " psi.service s WHERE psi.packageService = :ps)")
	void updateTheOriginalPrice(@Param("ps") PackageService ps);
}
