package housemate.entities;

import housemate.constants.ServiceConfiguration;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "service_config")
public class ServiceConfig {

	@Id
	@Column(name = "service_config_id")
	private int service_config_id;
	
	@Column(name = "config_type")
	private ServiceConfiguration configType;
	
	@Column(name = "config_value")
	private String configValue;
	
}
