package housemate.controllers;

import housemate.models.*;
import housemate.responses.EventRes;
import housemate.responses.PurchasedServiceRes;
import housemate.services.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * @author hdang09
 */
@RestController
@RequestMapping("/schedule")
@CrossOrigin
@Tag(name = "Schedule")
@SecurityRequirement(name = "bearerAuth")
public class ScheduleController {

    @Autowired
    ScheduleService service;

    @Operation(summary = "Get all schedule for the current customer")
    @GetMapping("/customer")
    public ResponseEntity<List<EventRes>> getScheduleForCustomer(HttpServletRequest request) {
        return service.getScheduleForCustomer(request);
    }

    @Operation(summary = "Get all schedule for the current staff")
    @GetMapping("/staff")
    public ResponseEntity<List<EventRes>> getScheduleForStaff(HttpServletRequest request) {
        return service.getScheduleForStaff(request);
    }

    @Operation(summary = "Get all schedule for the staff by staff ID (userId)")
    @GetMapping("/staff/{userId}")
    public ResponseEntity<List<EventRes>> getStaffScheduleByUserId(@PathVariable int userId) {
        return service.getStaffScheduleByUserId(userId);
    }

    @Operation(summary = "Get all purchased service for the current user")
    @GetMapping("/all-purchased")
    public ResponseEntity<Set<PurchasedServiceRes>> getAllPurchased(HttpServletRequest request) {
        return service.getAllPurchased(request);
    }

    @Operation(summary = "Create schedule")
    @PostMapping("/create")
    public ResponseEntity<String> createSchedule(HttpServletRequest request, @Valid @RequestBody ScheduleDTO scheduleDTO) {
        return service.createSchedule(request, scheduleDTO);
    }

}

