package housemate.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackNewDTO {

	@Positive(message = "The task Id must be postive")
	@Schema(description = "The task id")
	private int taskId;

	@Positive(message = "The service Id must be postive")
	@Schema(description = "The service id")
	private int serviceId;

	@NotEmpty(message = "Please, write something ")
	@Schema(defaultValue = "", description = "Give feedbackack")
	private String content;

	@Schema(defaultValue = "0", description = "Give the rating")
	@Min(value = 1, message = "Please give feedback rating from 1 to 5")
	@Max(value = 5, message = "Please give feedback rating from 1 to 5")
	private int rating;

}
