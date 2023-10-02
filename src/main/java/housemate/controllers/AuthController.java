/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.controllers;

import housemate.models.AccountDTO;
import housemate.services.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author hdang09
 */
@RestController
@RequestMapping("/auth") // TODO: Change "/auth" to "/api/auth"
@CrossOrigin
@Tag(name = "Authentication")
public class AuthController {

    @Autowired
    AuthService service;

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody AccountDTO.Login account) {
        return service.login(account);
    }
    
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody AccountDTO.Register account) {
        return service.register(account);
    }
    
    // TODO: Integrate forgot password
    @PostMapping("/forgot-password/{email}")
    public ResponseEntity<String> forgotPassword(@Valid @PathVariable String email) {
        return service.forgotPassword(email);
    }
    
    // TODO: Fix route mapping
    @GetMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody AccountDTO.Login account) {
        return service.resetPassword(account);
    }
}