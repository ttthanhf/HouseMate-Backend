package housemate.models;

import housemate.constants.Enum.TimeUnit;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeriodConfigNewDTO {

	@Min(value = 2, message = "Config cycle value in range 2-12")
	@Max(value = 12, message = "Config cycle value in range 2-12")
	@Schema(example = "3", description = "Set the cycle value for the cuycle month in range 2-12]")
	int configValue;

	TimeUnit configName;

	@PositiveOrZero(message = "Set the min proportion against the original price/per mont for this cycle")
	@Schema(example = "1.0", description = "Set the min proportion against the original price/per mont for this cycle")
	float min;

	@PositiveOrZero(message = "Set the max proportion against the original price/per mont for this cycle")
	float max;

}
