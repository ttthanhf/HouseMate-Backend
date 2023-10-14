/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.models;

import housemate.constants.Cycle;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Date;

/**
 *
 * @author ThanhF
 */
@Data
public class HourlyScheduleDTO {
    @Positive
    private int serviceId;

    @FutureOrPresent
    private Date pickupDate;

    @FutureOrPresent
    private Date receivedDate;

    private Cycle cycle;

    @NotNull
    @NotEmpty
    private String note;

}
