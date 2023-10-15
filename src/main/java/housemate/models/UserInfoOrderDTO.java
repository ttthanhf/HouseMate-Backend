/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.models;

import housemate.constants.RegexConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 *
 * @author ThanhF
 */
@Data
public class UserInfoOrderDTO {

    @Size(min = 2, max = 200, message = "address must be between 2 and 200 characters")
    @Schema(example = "Example", description = "Address")
    private String address;

    @Pattern(regexp = RegexConstants.PHONE_NUMBER_REGEX, message = "Invalid phone number format")
    @Schema(example = "0866123456", description = "Phone number")
    private String phone;

    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Schema(example = "Example", description = "Full name")
    private String fullName;

}
