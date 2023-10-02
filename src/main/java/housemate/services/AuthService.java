/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.services;

import housemate.entities.JwtPayload;
import housemate.entities.UserAccount;
import housemate.mappers.AccountMapper;
import housemate.mappers.JwtPayloadMapper;
import housemate.models.LoginAccountDTO;
import housemate.models.RegisterAccountDTO;
import housemate.repositories.UserRepository;
import housemate.utils.JwtUtil;
import java.net.URI;
import java.util.List;
import java.util.Map;
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

    @Autowired
    JwtUtil jwtUtil;

    @Value("${url.client}")
    private String redirectUri;

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

        // Generate token
        JwtPayload jwtPayload = new JwtPayloadMapper().mapFromUserAccount(accountDB);
        Map<String, Object> payload = jwtPayload.toMap();
        String token = jwtUtil.generateToken(payload);

        return ResponseEntity.status(HttpStatus.OK).body(token);

    }

    public ResponseEntity<List<UserAccount>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(userRepository.findAll());
    }

    public ResponseEntity<String> register(RegisterAccountDTO registerAccountDTO) {
        UserAccount accountDB = userRepository.findByEmailAddress(registerAccountDTO.getEmail());

        // Check email exists database
        if (accountDB != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This email have been created before");
        }

        // Insert to database
        UserAccount userAccount = new AccountMapper().mapToEntity(registerAccountDTO);
        userAccount = userRepository.save(userAccount);

        // Generate token
        JwtPayload jwtPayload = new JwtPayloadMapper().mapFromUserAccount(userAccount);
        Map<String, Object> payload = jwtPayload.toMap();
        String token = jwtUtil.generateToken(payload);

        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    public ResponseEntity<String> forgotPassword(String email) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("This feature will be upgraded soon!");
    }

    public ResponseEntity<String> setNewPassword(LoginAccountDTO loginAccountDTO) {
        UserAccount accountDB = userRepository.findByEmailAddress(loginAccountDTO.getEmail());

        // Check email not in database
        if (accountDB == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This email haven't created");
        }


        // Set new password


        accountDB.setToPasswordHash(loginAccountDTO.getPassword());
        userRepository.save(accountDB);
        return ResponseEntity.status(HttpStatus.OK).body("Set new password successfully!");
    }

    public ResponseEntity<String> loginWithGoogle(Map<String, Object> userOAuth) {
        String email = (String) userOAuth.get("email");
        String fullName = (String) userOAuth.get("name");
        boolean emailVerified = (boolean) userOAuth.get("email_verified");
        String avatar = (String) userOAuth.get("picture");

        UserAccount userAccount = userRepository.findByEmailAddress(email);

        //if userAccount not exist -> create new user
        if (userAccount == null) {
            UserAccount newUser = new UserAccount(fullName, email, emailVerified, avatar);
            userAccount = userRepository.save(newUser);
        }

        //Create jwt payload
        JwtPayload jwtPayload = new JwtPayload(userAccount.getUserId(), fullName, email, userAccount.getRole().toString());
        Map<String, Object> payload = jwtPayload.toMap();

        //generate token with payload
        String token = jwtUtil.generateToken(payload);

        //Create uri with token for redirect
        String url = redirectUri + "/" + "?success=true&token=" + token;
        URI uri = URI.create(url);
        return ResponseEntity.status(HttpStatus.FOUND).location(uri).build();
    }
}
