/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.controllers;

import housemate.entities.UserAccount;
import housemate.models.ForgotPasswordDTO;
import housemate.models.LoginAccountDTO;
import housemate.models.RegisterAccountDTO;
import housemate.models.ResetPasswordDTO;
import housemate.services.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
<<<<<<< HEAD
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
=======
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
>>>>>>> parent of 8b23159 (ADD - Add login, register, forgot password, reset new password request)

/**
 *
 * @author hdang09
 */
@RestController
@RequestMapping("/auth")  // TODO: Change "/auth" to "/api/auth"
@CrossOrigin
@Tag(name = "Authentication")
public class AuthController {

    @Autowired
    AuthService authService;

    @GetMapping("/all")
    public ResponseEntity<List<UserAccount>> getAll() {
        return authService.getAll();
    }

    @PostMapping("/login")
<<<<<<< HEAD
    public ResponseEntity<String> login(@Valid @RequestBody LoginAccountDTO account) {
        return authService.login(account);
=======
    public ResponseEntity<Void> login(@RequestBody LoginAccountDTO account) {
        return service.login(account);
>>>>>>> parent of 8b23159 (ADD - Add login, register, forgot password, reset new password request)
    }

    @PostMapping("/register")
<<<<<<< HEAD
    public ResponseEntity<String> register(@Valid @RequestBody RegisterAccountDTO account) {
        return authService.register(account);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordDTO forgotPasswordDTO) {
        return authService.forgotPassword(forgotPasswordDTO);
    }

    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
        return authService.resetPassword(resetPasswordDTO);
    }

    @GetMapping("/callback/google/redirect")
    public ResponseEntity<String> loginSuccessWithGoogle(OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        Map<String, Object> user = oAuth2AuthenticationToken.getPrincipal().getAttributes();
        return authService.loginWithGoogle(user);
    }
}
=======
    public ResponseEntity<String> register (@RequestBody RegisterAccountDTO account) {
        return service.register(account);
    }
}
>>>>>>> parent of 8b23159 (ADD - Add login, register, forgot password, reset new password request)
