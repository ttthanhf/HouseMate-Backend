package housemate.models;

import housemate.constants.RegexConstants;
import housemate.constants.Sex;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreateAccountDTO {
    @NotNull(message = "Full name cannot be null")
    @NotEmpty(message = "Full name cannot be empty")
    private String fullName;

    @NotNull(message = "Date of birth cannot be null")
    private LocalDate dateOfBirth;

    @NotNull(message = "Sex cannot be null")
    private Sex sex;

    @Schema(example = "0987654321", description = "Phone number of an account")
    @Pattern(regexp = RegexConstants.PHONE_NUMBER_REGEX, message = "Invalid phone number format")
    private String phone;

    @NotNull(message = "Identity card cannot be null")
    @Pattern(regexp = RegexConstants.IDENTITY_CARD, message = "Invalid identity card format")
    private String identityCard;

    @Email(message = "Please enter a valid email")
    @Schema(example = "example@gmail.com", description = "Email of an account")
    private String email;

    @NotNull(message = "Address cannot be null")
    private String address;
}
