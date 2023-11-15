package housemate.models;

import housemate.constants.Gender;
import housemate.constants.RegexConstants;
import housemate.constants.Role;
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
public class UpdateAccountDTO {
    @NotNull(message = "Full name cannot be null")
    @NotEmpty(message = "Full name cannot be empty")
    private String fullName;

    @NotNull(message = "Date of birth cannot be null")
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender cannot be null")
    private Gender gender;

    @Schema(example = "0866123456", description = "Phone number of an account")
    @Pattern(regexp = RegexConstants.PHONE_NUMBER_REGEX, message = "Invalid phone number format")
    private String phoneNumber;

    @NotNull(message = "Role cannot be null")
    private Role role;

    @NotNull(message = "Identity card cannot be null")
    @Pattern(regexp = RegexConstants.IDENTITY_CARD, message = "Invalid identity card format")
    private String identityCard;

    @Email(message = "Please enter a valid email")
    @Schema(example = "example@gmail.com", description = "Email of an account")
    private String email;

    @NotNull(message = "Address cannot be null")
    private String address;
}
