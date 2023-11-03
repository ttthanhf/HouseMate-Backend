package housemate.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import housemate.constants.Enum.TimeUnit;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "period_price_config")
public class PeriodPriceConfig {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "config_id")
	int configId;
	
	@Column(name = "config_value")
	int configValue;
	
	@Column(name = "config_name")
	@Enumerated(EnumType.STRING)
	TimeUnit configName;
	
	@Column(name = "min")
	float min;
	
	@Column(name = "max")
	float max;
	
	@Column(name = "date_start")
	LocalDateTime dateStart;
	
	@Column(name = "date_end")
	LocalDateTime dateEnd;

}
