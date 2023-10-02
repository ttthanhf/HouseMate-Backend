/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.models;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ThanhF
 */
@Getter
@Setter
public class CartAddDTO {

    @Positive
    @Hidden
    private int userId;

    @Positive
    private int serviceId;

    @Positive
    private int quanlity;

    @PastOrPresent
    @Hidden
    private LocalDateTime date;
}
