/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.models;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

/**
 *
 * @author ThanhF
 */
@Data
public class CartDTO {

    @Hidden
    private int userId;

    @Positive(message = "Service Id must be greater than 0")
    private int serviceId;

    @Positive(message = "Quantity must be greater than 0")
    private int quantity;

    @PositiveOrZero(message = "Period Id must be greater than 0")
    private int periodId;
}
