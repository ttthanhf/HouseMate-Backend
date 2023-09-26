/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.controllers;

import housemate.entities.UserAccount;
import housemate.models.LoginAccountDTO;
import housemate.models.RegisterAccountDTO;
import housemate.services.AuthService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author hdang09
 */
@RestController
@RequestMapping("/auth") // TODO: Change "/auth" to "/api/auth"
@CrossOrigin
public class AuthController {

    @Autowired
    AuthService service;
    
    @GetMapping("/all")
    public ResponseEntity<List<UserAccount>> getAll() {
        return service.getAll();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginAccountDTO account) {
        return service.login(account);
    }
    
    @PostMapping("/register")
    public ResponseEntity<String> register (@RequestBody RegisterAccountDTO account) {
        return service.register(account);
    }
}