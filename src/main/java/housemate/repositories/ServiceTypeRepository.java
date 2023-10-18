package housemate.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import housemate.entities.ServiceType;

public interface ServiceTypeRepository extends JpaRepository<ServiceType, Integer> {
	
	Optional<List<ServiceType>> findAllByServiceId(int serviceId);

	void deleteAllByServiceId(int serviceId);
}
