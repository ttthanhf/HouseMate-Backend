package housemate.controllers;

import housemate.constants.Role;
import housemate.entities.UserAccount;
import housemate.models.CreateAccountDTO;
import housemate.models.UpdateAccountDTO;
import housemate.services.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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

    @Operation(summary = "[ADMIN] Ban account by userId")
    @DeleteMapping("/ban/{userId}")
    public ResponseEntity<String> ban(HttpServletRequest request, @PathVariable int userId) {
        return service.ban(request, userId);
    }

    @Operation(summary = "[ADMIN] Make account inactive by userId")
    @DeleteMapping("/inactive/{userId}")
    public ResponseEntity<String> inactive(HttpServletRequest request, @PathVariable int userId) {
        return service.inactive(request, userId);
    }

    @Operation(summary = "[ADMIN] Update role of an account")
    @PutMapping("/role")
    public ResponseEntity<String> changeRole(HttpServletRequest request, @RequestParam int userId, @RequestParam Role role) {
        return service.changeRole(request, userId, role);
    }

    // TODO: Change type to List<CustomerRes>
    @Operation(summary = "[ADMIN] Manage customers (Full name, schedule count, spent amount, transaction count, join date)")
    @GetMapping("/customers")
    public ResponseEntity<?> getAllCustomer(HttpServletRequest request) {
        return service.getAllCustomer(request);
    }

    @Operation(summary = "[ADMIN] Get customer details")
    @GetMapping("/customers/{customerId}")
    public ResponseEntity<?> getCustomerDetail(
            HttpServletRequest request,
            @PathVariable int customerId,
            @RequestParam(value = "start", required = false) String start,
            @RequestParam(value = "end", required = false) String end
    ) {
        return service.getCustomerDetail(request, customerId, start, end);
    }

    @Operation(summary = "[ADMIN] Get staff details")
    @GetMapping("/staffs/{staffId}")
    public ResponseEntity<?> getStaffDetail(
            HttpServletRequest request,
            @PathVariable int staffId,
            @RequestParam(value = "start", required = false) String start,
            @RequestParam(value = "end", required = false) String end
    ) {
        return service.getStaffDetail(request, staffId, start, end);
    }

    // TODO: Change type to List<StaffRes>
    @Operation(summary = "[ADMIN] Mange staffs (Full name, proficiency score, staff status, task count, success rate)")
    @GetMapping("/staffs")
    public ResponseEntity<?> getAllStaff(HttpServletRequest request) {
        return service.getAllStaff(request);
    }


    @Operation(summary = "Get current logged in user info")
    @GetMapping("/current")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserAccount> getCurrentUser(HttpServletRequest request) {
        return service.getCurrentUser(request);
    }

    @Operation(summary = "[ADMIN] Create staff account")
    @PostMapping("/create-staff")
    public ResponseEntity<String> createStaffAccount(HttpServletRequest request, @Valid @RequestBody CreateAccountDTO createAccountDTO) {
        return service.createStaffAccount(request, createAccountDTO);
    }
}

