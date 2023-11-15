package housemate.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import housemate.entities.ServiceFeedback;

@Repository
public interface FeedbackRepository extends JpaRepository<ServiceFeedback, Integer>{
	
	@Query(value = "SELECT f FROM ServiceFeedback f WHERE f.serviceId = :serviceId "
			+ "ORDER BY f.rating DESC, f.createdAt DESC ")
	List<ServiceFeedback> findAllByServiceId(@Param("serviceId") int serviceId);

	@Query(value = "SELECT f FROM ServiceFeedback f WHERE f.serviceId = :serviceId "
				 + "AND f.rating = :ratingLevel "
			     + "ORDER BY f.rating DESC, f.createdAt DESC")
	List<ServiceFeedback> findAllByRating(@Param("serviceId") int serviceId, @Param("ratingLevel") int ratingLevel);
	
	@Query(value = "SELECT f FROM ServiceFeedback f WHERE " 
					+ "f.rating = :ratingLevel "
					+ "ORDER BY f.rating DESC, f.createdAt DESC " 
					+ "LIMIT 10")
	List<ServiceFeedback> findTopFeedback(@Param("ratingLevel") int ratingLevel);

	@Query(value = "SELECT COALESCE(AVG(f.rating),0) FROM ServiceFeedback f WHERE f.serviceId = :serviceId ")
	float getFeedbackAvgRating(@Param("serviceId") int serviceId);

	@Query(value = "SELECT COALESCE(COUNT(f.customerId),0) FROM ServiceFeedback f WHERE f.serviceId = :serviceId "
				 + "AND f.rating = :ratingLevel")
	int getNumOfReviewPerRatingLevel(@Param("serviceId") int serviceId, @Param("ratingLevel") int ratingLevel);

	@Query(value = "SELECT COALESCE(COUNT(f.customerId),0) FROM ServiceFeedback f WHERE f.serviceId = :serviceId ")
	int getNumOfReview(@Param("serviceId") int serviceId);

	@Query(value = "SELECT f FROM ServiceFeedback f WHERE "
				 + "f.serviceFeedbackId = :feedbackId "
				 + "AND f.customerId = :customerId "
				 + "AND f.taskId = :taskId "
				 + "AND f.serviceId = :serviceId ")
	ServiceFeedback findFeedback(
			@Param("feedbackId") int serviceFeedbackId,
			@Param("customerId") int customerId,
			@Param("taskId") int taskId,
			@Param("serviceId") int serviceId);
	
	ServiceFeedback findByCustomerIdAndTaskIdAndServiceId(int customerId, int taskId, int ServiceId);
	
	ServiceFeedback findByTaskId(int taskId);

}
