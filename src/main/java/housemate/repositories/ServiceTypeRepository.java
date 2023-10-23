package housemate.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import housemate.entities.ServiceType;

public interface ServiceTypeRepository extends JpaRepository<ServiceType, Integer> {
	
	@Query("SELECT st FROM ServiceType st WHERE st.serviceId > 0 AND st.serviceId = :serviceId")
	Optional<List<ServiceType>> findAllByServiceId(@Param("serviceId") int serviceId);

	void deleteAllByServiceId(int serviceId);
	
	@Modifying
	@Query("UPDATE ServiceType st SET st.serviceId = -1 WHERE st.serviceId = :serviceId")
	void updateServiceIdForRemove(@Param("serviceId") int serviceId);
}
