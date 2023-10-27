package housemate.models;


import housemate.constants.RegexConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
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
<<<<<<< HEAD

    @NotNull(message = "Email must not be null")
    @Email(message = "Please enter a valid email")
    @Schema(example = "example@gmail.com", description = "Email of an account")
    public String email;

    @NotNull(message = "Password must not be null")
    @Pattern(
            regexp = RegexConstants.PASSWORD_REGEX,
            message = "Password must be 8 to 16 characters, include a number, an uppercase letter, and a lowercase letter"
    )
    @Schema(example = "Password123", description = "Password of an account")
=======
    @Pattern(regexp = "[a-zA-Z ]*", message = "Please enter a valid email!")
    public String email;
    
    @Pattern(
            regexp = "/^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$/", 
            message = "Must be at least 8 characters, include a number, an uppercase letter, and a lowercase letter."
    )
>>>>>>> parent of 8b23159 (ADD - Add login, register, forgot password, reset new password request)
    public String password;
}
