/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

    @Schema(example = "0866123456 or +84866123456", description = "Phone number of an account")
    @Pattern(regexp = "^(0|\\+?84)(3|5|7|8|9)[0-9]{8}$", message = "Invalid phone number format")
    private String phoneNumber;

    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,16}$",
            message = "Must be 8 to 16 characters, include a number, an uppercase letter, and a lowercase letter"
    )
    @Schema(example = "Password123", description = "Password of an account")
    private String password;
}
