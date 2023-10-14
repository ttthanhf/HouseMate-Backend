/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.models;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 *
 * @author ThanhF
 */
@Data
public class CartUpdateDTO {

    @Hidden
    private int userId;

    @Positive(message = "Service Id must be greater than 0")
    private int serviceId;

    @Positive(message = "Quantity must be greater than 0")
    @Max(value = 3)
    private int quantity;

    @Positive(message = "Period Id must be greater than 0")
    private int periodId;
}
