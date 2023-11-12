package housemate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import housemate.entities.Staff;

public interface StaffRepository extends JpaRepository<Staff, Integer>{
}
