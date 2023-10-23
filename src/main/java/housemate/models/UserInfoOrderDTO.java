/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 *
 * @author ThanhF
 */
@Data
public class UserInfoOrderDTO {

    @Size(min = 2, max = 200, message = "Address must be between 2 and 200 characters")
    @Schema(example = "Example", description = "Address")
    private String address;

    @Size(min = 10, max = 12)
    @Schema(example = "0909990099", description = "Phone")
    private String phone;

    @Schema(example = "vnpay", description = "Payment Method")
    private String paymentMethod;

}
