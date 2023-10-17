package housemate.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import housemate.entities.ServiceFeedback;

@Repository
public interface FeedbackRepository extends JpaRepository<ServiceFeedback, Integer>{
	List<ServiceFeedback> findAllByServiceId(int serviceId);
}
