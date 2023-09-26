/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.services;

import housemate.entities.UserAccount;
import housemate.models.LoginAccountDTO;
import housemate.models.RegisterAccountDTO;
import housemate.repositories.UserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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

    public ResponseEntity<Void> login(LoginAccountDTO loginAccountDTO) {
        return null;
    }
    
    public ResponseEntity<List<UserAccount>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(userRepository.findAll());
    }

    public ResponseEntity<String> register(RegisterAccountDTO registerAccountDTO) {
        UserAccount userAccount = new UserAccount().fromRegisterAccountDTO(registerAccountDTO);
        userRepository.save(userAccount);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Register successfully!");
    }
}