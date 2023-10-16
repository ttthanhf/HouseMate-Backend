/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.models;

import housemate.constants.Cycle;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 *
 * @author hdang09
 */
@Data
public class DeliveryScheduleDTO {
    private Cycle cycle = Cycle.ONLY_ONE_TIME;  // Default: Only one time

    @FutureOrPresent(message = "Date must be in the present or future")
    private LocalDate date;

    private String note;

    @Min(value = 1, message = "Quantity must be larger than 0")
    @Max(value = 10, message = "Quantity must be less than 10")
    private int quantity = 1;  // Default: 1

    @Positive(message = "Service ID must be a positive number")
    private int serviceId;

    @FutureOrPresent(message = "Time must be in the present or future")
    private LocalTime time;

    @NotNull(message = "Type ID must not be null")
    private int typeId;
}
