/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.controllers;

import housemate.services.LoginService;
import java.util.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

/**
 *
 * @author ThanhF
 */
@RestController
@RequestMapping
@CrossOrigin
public class LoginController {

    private final String redirectUri;

    @Autowired
    LoginService loginService;

    public LoginController(@Value("${application.setting.google.redirect-uri}") String redirectUri) {
        this.redirectUri = redirectUri;
    }

    @GetMapping("/callback/google/redirect")
    public ResponseEntity<String> loginSuccessWithGoogle(OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        if (oAuth2AuthenticationToken != null) {
            Map<String, Object> user = oAuth2AuthenticationToken.getPrincipal().getAttributes();
            String token = loginService.loginWithGoogle(user);
            String url = redirectUri + "/" + "?success=true&token=" + token;
            URI uri = URI.create(url);
            return ResponseEntity.status(HttpStatus.FOUND).location(uri).build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
    }
}
