/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.entities;

import housemate.models.RegisterAccountDTO;
import jakarta.persistence.*;
import lombok.*;
import org.mindrot.jbcrypt.BCrypt;

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
    @Column(name = "phone_number", nullable = true, unique=true)
    private int phoneNumber;
>>>>>>> 0cbd3bf (Merge pull request #4 from ttthanhf/feature/loginWithGoogle)

    @Column(name = "password_hash", nullable = true)
    private String passwordHash;

    @Column(name = "email_address", unique = true)
    private String emailAddress;

    @Column(name = "email_validation_status")
    private boolean emailValidationStatus;

    public UserAccount(String fullName, String emailAddress, boolean emailValidationStatus) {
        this.fullName = fullName;
        this.emailAddress = emailAddress;
        this.emailValidationStatus = emailValidationStatus;
    }

    public UserAccount fromRegisterAccountDTO(RegisterAccountDTO registerAccountDTO) {

        UserAccount userAccount = new UserAccount();

        userAccount.setEmailAddress(registerAccountDTO.getEmail());
        userAccount.setFullName(registerAccountDTO.getFullName());
        userAccount.setPhoneNumber(registerAccountDTO.getPhoneNumber());
        userAccount.setToPasswordHash(registerAccountDTO.getPassword());
        userAccount.setRole(Role.CUSTOMER);
        userAccount.setEmailValidationStatus(false);

        return userAccount;
    }

    public void setToPasswordHash(String password) {
        final int LOG_ROUNDS = 12;
        String hash = BCrypt.hashpw(password, BCrypt.gensalt(LOG_ROUNDS));

        this.passwordHash = hash;
    }

}
