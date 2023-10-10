package housemate.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackNewDTO {
	
    @NotNull
    @Positive(message = "The task must not be empty")
    @Schema(description = "The task id")
    private int taskId;

    @NotNull
    @Positive(message = "The service id must not be empty")
    @Schema(description = "The service id")
    private int serviceId;

    
    @NotEmpty
    @Schema( defaultValue = "", description = "Give feedbackack")
    private int content;

    
    @NotEmpty(message = "Give feedback rating ")
    @Schema( defaultValue = "0", description = "Give the rating")
    private Float rating;

}
