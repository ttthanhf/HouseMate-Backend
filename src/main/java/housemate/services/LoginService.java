/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.services;

import housemate.entities.JwtPayload;
import housemate.entities.UserAccount;
import housemate.repositories.UserRepository;
import housemate.utils.JwtUtil;
import java.net.URI;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 *
 * @author ThanhF
 */
@Service
public class LoginService {

    @Autowired
    private UserRepository userRepository;

    private final String redirectUri;

    public LoginService(@Value("${application.setting.google.redirect-uri}") String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public ResponseEntity<String> loginWithGoogle(Map<String, Object> userOAuth) {
        String email = (String) userOAuth.get("email");
        String fullName = (String) userOAuth.get("name");
        boolean emailVerified = (boolean) userOAuth.get("email_verified");
        UserAccount userAccount = userRepository.findByEmailAddress(email);
        if (userAccount == null) {
            UserAccount newUser = new UserAccount(fullName, email, emailVerified);
            userAccount = userRepository.save(newUser);
        }
        JwtUtil jwtUtil = new JwtUtil();
        JwtPayload jwtPayload = new JwtPayload(userAccount.getUserId(), fullName, email, userAccount.getRole().toString());
        Map<String, Object> payload = jwtPayload.toMap();
        String token = jwtUtil.generateToken(payload);
        String url = redirectUri + "/" + "?success=true&token=" + token;
        URI uri = URI.create(url);
        return ResponseEntity.status(HttpStatus.FOUND).location(uri).build();
    }
}
