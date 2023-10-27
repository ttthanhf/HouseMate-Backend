package housemate.models;

import housemate.constants.RegexConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UpdateAccountDTO {
    @NotNull(message = "Full name cannot be null")
    @NotEmpty(message = "Full name cannot be empty")
    private String fullName;

    @Schema(example = "0866123456", description = "Phone number of an account")
    @Pattern(regexp = RegexConstants.PHONE_NUMBER_REGEX, message = "Invalid phone number format")
    private String phoneNumber;

    @Email(message = "Please enter a valid email")
    @Schema(example = "example@gmail.com", description = "Email of an account")
    private String emailAddress;

    @NotNull(message = "Avatar cannot be null")
    @NotEmpty(message = "Avatar cannot be empty")
    private String avatar;
}
