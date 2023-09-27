/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
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
public class LoginAccountDTO {

    @Pattern(
            regexp = "/^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$/",
            message = "Please enter a valid email!"
    )
    @Schema(example = "example@gmail.com", description = "Email of an account")
    public String email;

    @Pattern(
            regexp = "/^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$/",
            message = "Must be at least 8 characters, include a number, an uppercase letter, and a lowercase letter."
    )
    @Schema(example = "Example123", description = "Password of an account")
    public String password;
}
