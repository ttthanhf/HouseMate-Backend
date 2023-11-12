package housemate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import housemate.entities.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
	
}
