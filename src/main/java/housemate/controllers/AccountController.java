package housemate.controllers;

import housemate.constants.Role;
import housemate.entities.UserAccount;
import housemate.services.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@RequestMapping("/account")
@CrossOrigin
@Tag(name = "Account")
@SecurityRequirement(name = "bearerAuth")
public class AccountController {

    @Autowired
    AccountService service;

    @Operation(summary = "Get all account (customer, staff, admin)")
    @GetMapping("/all")
    public ResponseEntity<List<UserAccount>> getAll() {
        return service.getAll();
    }

    @Operation(summary = "Get info of an account by userId")
    @GetMapping("/info/{userId}")
    public ResponseEntity<UserAccount> getInfo(@PathVariable int userId) {
        return  service.getInfo(userId);
    }

    @Operation(summary = "Update account info")
    @PutMapping("/update")
    public ResponseEntity<String> updateInfo(@RequestBody UserAccount account) {
        return service.updateInfo(account);
    }

    @Operation(summary = "Delete account by userId")
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<String> updateInfo(@PathVariable int userId) {
        return service.delete(userId);
    }

    @Operation(summary = "Update role of an account")
    @PutMapping("/role")
    public ResponseEntity<String> changeRole(@RequestParam int userId, @RequestParam Role role) {
        return service.changeRole(userId, role);
    }

    @Operation(summary = "Get all customer account")
    @GetMapping("/customers")
    public ResponseEntity<List<UserAccount>> getAllCustomer() {
        return service.getAllCustomer();
    }

    @Operation(summary = "Get all staff account")
    @GetMapping("/staffs")
    public ResponseEntity<List<UserAccount>> getAllStaff() {
        return service.getAllStaff();
    }

    @Operation(summary = "Get all admin account")
    @GetMapping("/admin")
    public ResponseEntity<List<UserAccount>> getAllAdmin() {
        return service.getAllAdmin();
    }
}

