package housemate.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import housemate.constants.ServiceConfiguration;
import housemate.entities.ServiceConfig;

@Repository
public interface ServiceConfigRepository extends JpaRepository<ServiceConfig, Integer> {

	@Query("SELECT * FROM service WHERE" 
			+ " LOWER(configType) COLLATE utf8_bin = LOWER(:configType)"
			+ " LOWER(configValue) COLLATE utf8_bin = LOWER(:configValue)")
	Optional<ServiceConfig> findByConfigTypeAndConfigValue(
			@Param("configType") ServiceConfiguration configType,
			@Param("configValue") String configValue);
	
	List<ServiceConfig> findAllByConfigType(ServiceConfiguration configType);

}
