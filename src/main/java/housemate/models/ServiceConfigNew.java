package housemate.models;

import housemate.constants.ServiceConfiguration;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ServiceConfigNew {
	
	@NotEmpty
	private ServiceConfiguration configType;
	
	@NotEmpty
	private String configValue;

}
