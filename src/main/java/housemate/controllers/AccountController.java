package housemate.controllers;

import housemate.constants.Role;
import housemate.entities.UserAccount;
import housemate.models.UpdateAccountDTO;
import housemate.responses.CustomerRes;
import housemate.responses.StaffRes;
import housemate.services.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
    @PutMapping("/update/{userId}")
    public ResponseEntity<String> updateInfo(@Valid @RequestBody UpdateAccountDTO updateAccountDTO, @PathVariable int userId) {
        return service.updateInfo(updateAccountDTO, userId);
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

    @Operation(summary = "Manage customers (Full name, Schedule count, Spent amount, Transaction count, Join date )")
    @GetMapping("/customers")
    public ResponseEntity<List<CustomerRes>> getAllCustomer() {
        return service.getAllCustomer();
    }

    @Operation(summary = "Mange staffs (Full name, Proficiency score, Staff status, Task count)")
    @GetMapping("/staffs")
    public ResponseEntity<List<StaffRes>> getAllStaff() {
        return service.getAllStaff();
    }

    @Operation(summary = "Get all admin account")
    @GetMapping("/admins")
    public ResponseEntity<List<UserAccount>> getAllAdmin() {
        return service.getAllAdmin();
    }

    @Operation(summary = "Get current logged in user info")
    @GetMapping("/current")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserAccount> getCurrentUser(HttpServletRequest request) {
        return service.getCurrentUser(request);
    }
}

