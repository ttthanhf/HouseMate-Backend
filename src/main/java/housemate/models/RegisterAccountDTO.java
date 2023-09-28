/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.models;

import housemate.constants.Regex;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author hdang09
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RegisterAccountDTO {

    @Email(message = "Please enter a valid email")
    @Schema(example = "example@gmail.com", description = "Email of an account")
    private String email;

    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 character")
    @Schema(example = "Example", description = "Full name of an account")
    private String fullName;

    @Schema(example = "0866123456", description = "Phone number of an account")
    @Pattern(regexp = Regex.PHONE_NUMBER, message = "Invalid phone number format")
    private String phoneNumber;

    @Pattern(
            regexp = Regex.PASSWORD,
            message = "Must be 8 to 16 characters, include a number, an uppercase letter, and a lowercase letter"
    )
    @Schema(example = "Password123", description = "Password of an account")
    private String password;
}
