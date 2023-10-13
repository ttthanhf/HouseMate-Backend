/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.models;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 *
 * @author ThanhF
 */
@Data
public class CartAddDTO {

    @Positive
    @Hidden
    private int userId;

    @Positive
    private int serviceId;

    @Positive
    private int quantity;

    @Positive
    private int periodId;
}
