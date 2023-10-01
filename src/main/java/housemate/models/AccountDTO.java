package housemate.models;

import housemate.constants.RegexConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

public class AccountDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Login {

        @Email(message = "Please enter a valid email")
        @Schema(example = "example@gmail.com", description = "Email of an account")
        public String email;

        @Pattern(
                regexp = RegexConstants.PASSWORD_REGEX,
                message = "Must be 8 to 16 characters, include a number, an uppercase letter, and a lowercase letter"
        )
        @Schema(example = "Password123", description = "Password of an account")
        public String password;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Register {

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
        private String password;
    }

}
