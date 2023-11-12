package housemate.entities;

import housemate.constants.ServiceConfiguration;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "service_config")
public class ServiceConfig {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "service_config_id")
	private int serviceConfigId;
	
	@Column(name = "config_type")
	@Enumerated(EnumType.STRING)
	private ServiceConfiguration configType;
	
	@Column(name = "config_value")
	private String configValue;	
}
