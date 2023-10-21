/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import housemate.constants.Cycle;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * @author hdang09
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReturnScheduleDTO {

    private Cycle cycle = Cycle.ONLY_ONE_TIME; // Default: Only one time

    private String note;

//    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @FutureOrPresent(message = "Pickup date must be in the present or future")
    private LocalDate pickupDate;

//    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @FutureOrPresent(message = "Received date must be in the present or future")
    private LocalDate receivedDate;

    @DateTimeFormat(pattern = "HH:mm")
    @Schema(example = "11:25")
    private LocalTime time; // Pickup time

    @DateTimeFormat(pattern = "HH:mm")
    @Schema(example = "13:25")
    private LocalTime receivedTime;

    @Positive(message = "Service ID must be a positive number")
    private int serviceId;

    @Positive(message = "Type ID must be a positive number")
    private int typeId;

}
