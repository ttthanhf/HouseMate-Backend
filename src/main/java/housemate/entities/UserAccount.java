/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.entities;

import jakarta.persistence.*;
import java.security.Timestamp;

/**
 *
 * @author ThanhF
 */
@Entity
@Table(name = "User_Account")
public class UserAccount {

    @Id
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
    private Timestamp tokenGenerationTime;

    @Column(name = "email_validation_status")
    private String emailValidationStatus;

    @Column(name = "authentication_provider_name")
    private String authenticationProviderName;

    @Column(name = "authentication_provider_token")
    private String authenticationProviderToken;

    @Column(name = "password_recovery_token")
    private String passwordRecoveryToken;

    @Column(name = "recovery_token_time")
    private Timestamp recoveryTokenTime;

    public UserAccount(int userId, String role, String fullName, int phoneNumber, String loginName, String passwordHash, String emailAddress, String confirmationToken, Timestamp tokenGenerationTime, String emailValidationStatus, String authenticationProviderName, String authenticationProviderToken, String passwordRecoveryToken, Timestamp recoveryTokenTime) {
        this.userId = userId;
        this.role = role;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.loginName = loginName;
        this.passwordHash = passwordHash;
        this.emailAddress = emailAddress;
        this.confirmationToken = confirmationToken;
        this.tokenGenerationTime = tokenGenerationTime;
        this.emailValidationStatus = emailValidationStatus;
        this.authenticationProviderName = authenticationProviderName;
        this.authenticationProviderToken = authenticationProviderToken;
        this.passwordRecoveryToken = passwordRecoveryToken;
        this.recoveryTokenTime = recoveryTokenTime;
    }

    
}
