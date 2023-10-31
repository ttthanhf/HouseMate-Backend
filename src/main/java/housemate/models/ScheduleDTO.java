package housemate.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import housemate.constants.Cycle;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleDTO {
    @Positive(message = "Service ID must be a positive number")
    private int serviceId;

    @NotNull(message = "Group type must not be null")
    private String groupType;

    private Cycle cycle = Cycle.ONLY_ONE_TIME;  // Default: Only one time

    private String note;

    private int typeId;

    @Min(value = 0, message = "Quantity must be larger than 0")
    @Max(value = 10, message = "Quantity must be less than 10")
    private int quantityRetrieve = 1;

    @FutureOrPresent(message = "Start date must be in the present or future")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm") // TODO: Change format base on front-end
    @Schema(example = "03/11/2023 12:30")
    private LocalDateTime startDate;

    @FutureOrPresent(message = "End date must be in the present or future")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm") // TODO: Change format base on front-end
    @Schema(example = "03/11/2023 15:30")
    private LocalDateTime endDate;

    @NotNull(message = "User usage ID must not be null")
    private int userUsageId;
}
