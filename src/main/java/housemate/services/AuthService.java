/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.services;

import housemate.constants.Role;
import housemate.entities.UserAccount;
import housemate.models.LoginAccountDTO;
import housemate.models.RegisterAccountDTO;
import housemate.repositories.UserRepository;
import java.net.URI;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 *
 * @author hdang09
 */
@Service
public class AuthService {

    @Autowired
    UserRepository userRepository;

    @Value("${url.client}")
    String URL_CLIENT;

    public ResponseEntity<String> login(LoginAccountDTO loginAccountDTO) {
        UserAccount accountDB = userRepository.findByEmailAddress(loginAccountDTO.getEmail());

        // Check email not in database
        if (accountDB == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This email haven't created");
        }

        // Check correct password
        boolean isCorrect = BCrypt.checkpw(loginAccountDTO.getPassword(), accountDB.getPasswordHash());
        if (!isCorrect) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email or password not correct");
        }

        // TODO: Generate token
        String exampleToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        String token = exampleToken;
        return ResponseEntity.status(HttpStatus.OK).body(token);

    }

    public ResponseEntity<List<UserAccount>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(userRepository.findAll());
    }

    public ResponseEntity<UserAccount> register(RegisterAccountDTO registerAccountDTO) {
        UserAccount userAccount = new UserAccount().fromRegisterAccountDTO(registerAccountDTO);
        userAccount = userRepository.save(userAccount);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(userAccount);
    }

    public ResponseEntity<String> forgotPassword(String email) {
        // TODO: Integrate forgot password
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("This feature will be upgraded soon...");
    }

    public ResponseEntity<String> setNewPassword(LoginAccountDTO loginAccountDTO) {
        UserAccount accountDB = userRepository.findByEmailAddress(loginAccountDTO.getEmail());

        // Check email not in database
        if (accountDB == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This email haven't created");
        }

        accountDB.setToPasswordHash(loginAccountDTO.getPassword());
        userRepository.save(accountDB);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Set new password successfully!");
    }
}
