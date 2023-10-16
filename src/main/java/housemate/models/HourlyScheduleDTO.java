/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.models;

import housemate.constants.Cycle;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

/**
 * @author hdang09
 */
@Data
public class HourlyScheduleDTO {

    private Cycle cycle = Cycle.ONLY_ONE_TIME;  // Default: Only one time

    @FutureOrPresent(message = "Date must be in the present or future")
    private LocalDate date;

    private String note;

    @Positive(message = "Service ID must be a positive number")
    private int serviceId;

    @NotNull(message = "Time ranges must not be null")
    @Schema(example = "[\"13:25\", \"15:25\"]")
    private Set<LocalTime> timeRanges;

    @NotNull(message = "Type must not be null")
    @NotEmpty(message = "Type must not be empty")
    private int typeId;
}
