package housemate.models;

import housemate.constants.ServiceConfiguration;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ServiceConfigNew {

	@NotNull
	private ServiceConfiguration configType;
	
	@NotEmpty
	private String configValue;

}
