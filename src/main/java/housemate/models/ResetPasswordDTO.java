package housemate.models;

import housemate.constants.RegexConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResetPasswordDTO {

    @Size(min = 30, max = 30, message = "Invalid token!")
    @Schema(example = "----TOKEN-FROM-URL-----", description = "Token from URL in mail")
    private String token;

    @Pattern(
            regexp = RegexConstants.PASSWORD_REGEX,
            message = "Must be 8 to 16 characters, include a number, an uppercase letter, and a lowercase letter"
    )
    @Schema(example = "Password123", description = "Password of an account")
    private String password;
}
