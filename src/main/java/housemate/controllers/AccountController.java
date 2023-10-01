package housemate.controllers;

import housemate.constants.Role;
import housemate.entities.UserAccount;
import housemate.services.AccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 * @author hdang09
 */
@RestController
@RequestMapping("/account") // TODO: Change "/auth" to "/api/auth"
@CrossOrigin
@Tag(name = "Account")
public class AccountController {

    @Autowired
    AccountService service;

    @GetMapping("/all")
    public ResponseEntity<List<UserAccount>> getAll() {
        return service.getAll();
    }

    @GetMapping("/info/{userId}")
    public ResponseEntity<UserAccount> getInfo(@PathVariable int userId) {
        return  service.getInfo(userId);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateInfo(@RequestBody UserAccount account) {
        return service.updateInfo(account);
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<String> updateInfo(@PathVariable int userId) {
        return service.delete(userId);
    }

    @PutMapping("role")
    public ResponseEntity<String> changeRole(@RequestParam int userId, @RequestParam Role role) {
        return service.changeRole(userId, role);
    }
}

