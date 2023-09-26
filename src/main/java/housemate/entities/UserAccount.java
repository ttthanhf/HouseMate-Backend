/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

/**
 *
 * @author ThanhF
 */
@Entity
@Table(name = "UserAccount")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;

    @Column(name = "role")
    private String role;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "phone_number", unique = true)
    private int phoneNumber;

    @Column(name = "login_name", unique = true)
    private String loginName;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "email_address", unique = true)
    private String emailAddress;

    @Column(name = "confirmation_token")
    private String confirmationToken;

    @Column(name = "token_generation_time")
    private LocalDateTime tokenGenerationTime;

    @Column(name = "email_validation_status")
    private String emailValidationStatus;

    @Column(name = "authentication_provider_name")
    private String authenticationProviderName;

    @Column(name = "authentication_provider_token")
    private String authenticationProviderToken;

    @Column(name = "password_recovery_token")
    private String passwordRecoveryToken;

    @Column(name = "recovery_token_time")
    private LocalDateTime recoveryTokenTime;

    public UserAccount(String fullName, String emailAddress, String emailValidationStatus) {
        this.fullName = fullName;
        this.emailAddress = emailAddress;
        this.emailValidationStatus = emailValidationStatus;
    }
}
