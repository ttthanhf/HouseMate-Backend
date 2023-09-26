/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.services;

import housemate.entities.JwtPayload;
import housemate.entities.UserAccount;
import housemate.repositories.UserRepository;
import housemate.utils.JwtUtil;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
//import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;

/**
 *
 * @author ThanhF
 */
@Service
public class LoginService {

    @Autowired
    private UserRepository userRepository;

    public String loginWithGoogle(Map<String, Object> userOAuth) {
        String email = (String) userOAuth.get("email");
        String fullName = (String) userOAuth.get("name");
        String userId = (String) userOAuth.get("sub");
        String emailVerified = (String) userOAuth.get("email_verified").toString();
        String imgURL = (String) userOAuth.get("picture");
//        Optional<UserAccount> userAccount = userRepository.findById(1);
//        UserAccount userAccount = userRepository.findByEmailAddress(email);
//        if (userAccount == null) {
//            UserAccount newUser = new UserAccount(fullName, email, emailVerified);
//            userAccount = userRepository.save(newUser);
//        }
        JwtUtil jwtUtil = new JwtUtil();
        JwtPayload jwtPayload = new JwtPayload(123, fullName, email, "customer");
        Map<String, Object> payload = jwtPayload.toMap();
        String token = jwtUtil.generateToken(payload);
        return token;
    }
}
