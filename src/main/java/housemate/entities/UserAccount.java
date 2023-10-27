/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import housemate.constants.Role;
import jakarta.persistence.*;
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
    @Enumerated(EnumType.STRING)
    private Role role = Role.CUSTOMER;

    @Column(name = "full_name")
    private String fullName;

<<<<<<< HEAD
    @Column(name = "phone_number", nullable = true, unique = true)
    private String phoneNumber;
=======
    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

//    @Column(name = "login_name", unique = true)
//    private String loginName;
>>>>>>> parent of 8b23159 (ADD - Add login, register, forgot password, reset new password request)

    @JsonIgnore
    @Column(name = "password_hash", nullable = true)
    private String passwordHash;

    @Column(name = "email_address", unique = true)
    private String emailAddress;

//    @Column(name = "confirmation_token")
//    private String confirmationToken;

//    @Column(name = "token_generation_time")
//    private LocalDateTime tokenGenerationTime;

    @Column(name = "email_validation_status")
    private boolean emailValidationStatus;

<<<<<<< HEAD
    @Column(name = "avatar")
    private String avatar;

    @Column(name = "address")
    private String address;

    @JsonIgnore
    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    public UserAccount(String fullName, String emailAddress, boolean emailValidationStatus, String avatar) {
=======
//    @Column(name = "authentication_provider_name")
//    private String authenticationProviderName;
//
//    @Column(name = "authentication_provider_token")
//    private String authenticationProviderToken;
//
//    @Column(name = "password_recovery_token")
//    private String passwordRecoveryToken;

//    @Column(name = "recovery_token_time")
//    private LocalDateTime recoveryTokenTime;

    public UserAccount(String fullName, String emailAddress, boolean emailValidationStatus) {
>>>>>>> parent of 8b23159 (ADD - Add login, register, forgot password, reset new password request)
        this.fullName = fullName;
        this.emailAddress = emailAddress;
        this.emailValidationStatus = emailValidationStatus;
        this.avatar = avatar;
    }

<<<<<<< HEAD
=======
    public UserAccount fromRegisterAccountDTO(RegisterAccountDTO registerAccountDTO) {
        final int LOG_ROUNDS = 12;
        UserAccount userAccount = new UserAccount();

        userAccount.setEmailAddress(registerAccountDTO.getEmail());
        userAccount.setFullName(registerAccountDTO.getFullName());
        userAccount.setPhoneNumber(registerAccountDTO.getPhoneNumber());
        
        String hash = BCrypt.hashpw(registerAccountDTO.getPassword(), BCrypt.gensalt(LOG_ROUNDS));
        userAccount.setPasswordHash(hash);
        
        userAccount.setRole(Role.CUSTOMER);
        userAccount.setEmailValidationStatus(false);
        
        return  userAccount;
    }
>>>>>>> parent of 8b23159 (ADD - Add login, register, forgot password, reset new password request)
}
