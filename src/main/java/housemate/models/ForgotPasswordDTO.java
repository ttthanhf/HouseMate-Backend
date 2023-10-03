package housemate.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ForgotPasswordDTO {
    @Email(message = "Please enter a valid email")
    @Schema(example = "example@gmail.com", description = "Email of an account")
    private String email;
}
