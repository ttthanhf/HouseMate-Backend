package housemate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import housemate.entities.ServiceFeedback;

@Repository
public interface ServiceFeedbackRepository extends JpaRepository<ServiceFeedback, Integer>{

}
