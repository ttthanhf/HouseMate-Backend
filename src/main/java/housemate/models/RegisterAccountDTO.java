/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.models;

<<<<<<< HEAD
import housemate.constants.RegexConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
=======
>>>>>>> 0cbd3bf (Merge pull request #4 from ttthanhf/feature/loginWithGoogle)
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
<<<<<<< HEAD

    @Email(message = "Please enter a valid email")
    @Schema(example = "example@gmail.com", description = "Email of an account")
    private String email;

    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Schema(example = "Example", description = "Full name of an account")
    private String fullName;

    @Schema(example = "0866123456", description = "Phone number of an account")
    @Pattern(regexp = RegexConstants.PHONE_NUMBER_REGEX, message = "Invalid phone number format")
    private String phoneNumber;

    @Pattern(
            regexp = RegexConstants.PASSWORD_REGEX, 
            message = "Must be 8 to 16 characters, including a number, an uppercase letter, and a lowercase letter"
    )
    @Schema(example = "Password123", description = "Password of an account")
=======
//   @Pattern(
//           regexp = "/^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$/", 
//           message = "Please enter a valid email"
//   )

    private String email;

//   @Min(value = 2)
//   @Max(value = 50)
    private String fullName;

//   @Pattern(
//           regexp = "/^(0|\\+?84)(3|5|7|8|9)[0-9]{8}$/", 
//           message = "Please enter a valid phone number."
//   )
    private int phoneNumber;

//   @Pattern(
//           regexp = "/^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,16}$/", 
//           message = "Must be 8 to 16 characters, include a number, an uppercase letter, and a lowercase letter"
//   )
>>>>>>> 0cbd3bf (Merge pull request #4 from ttthanhf/feature/loginWithGoogle)
    private String password;
}
