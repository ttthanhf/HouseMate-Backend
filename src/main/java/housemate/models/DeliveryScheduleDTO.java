/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.models;

import housemate.constants.Cycle;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.Date;

/**
 *
 * @author ThanhF
 */
@Data
public class DeliveryScheduleDTO {
    @Positive
    private int serviceId;

    @FutureOrPresent
    private Date deliveryDate;

    private String type; // TODO: Fix type of this

    @Positive // TODO: Validate max for quantity
    private int quantity;

    private Cycle cycle;

    @NotNull
    @NotEmpty
    private String note;
}
