package housemate.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import housemate.constants.Enum.TimeUnit;
import housemate.entities.PeriodPriceConfig;

@Repository
public interface PeriodPriceConfigRepository extends JpaRepository<PeriodPriceConfig, Integer> {

	@Query("SELECT p FROM PeriodPriceConfig p WHERE :datetimeNow BETWEEN p.dateStart AND p.dateEnd ")
	List<PeriodPriceConfig> findAllInUsed(@Param("datetimeNow") LocalDateTime dateTimeNow);
	
	@Query("SELECT p FROM PeriodPriceConfig p WHERE :datetimeNow BETWEEN p.dateStart AND p.dateEnd ")
	List<PeriodPriceConfig> findByConfigValue(@Param("datetimeNow") LocalDateTime dateTimeNow);
	
	@Query("SELECT p FROM PeriodPriceConfig p "
			+ "WHERE :datetimeNow BETWEEN p.dateStart AND p.dateEnd "
			+ "AND p.configValue = :configValue "
			+ "AND p.configName = :configName ")
	PeriodPriceConfig findByConfigValueAndConfigName(
			@Param("datetimeNow") LocalDateTime dateTimeNow, 
			@Param("configValue")int configValue,
			@Param("configName") TimeUnit configName);
	
	PeriodPriceConfig findByConfigId(int ConfigId);
}
