package housemate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import housemate.entities.ServiceOrderItem;

@Repository
public interface ServiceOrderItemRepository extends JpaRepository<ServiceOrderItem, Integer>{

}
