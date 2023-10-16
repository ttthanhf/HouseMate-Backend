package housemate.controllers;

import housemate.entities.Schedule;
import housemate.models.*;
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

    @Operation(summary = "Get all schedule for the current user")
    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<ScheduleEventDTO>> getScheduleForUser(HttpServletRequest request) {
        return service.getScheduleForUser(request);
    }

    @Operation(summary = "Get schedule by ID")
    @GetMapping("/{scheduleId}")
    public ResponseEntity<Schedule> getScheduleById(@PathVariable int scheduleId) {
        return service.getScheduleById(scheduleId);
    }

    @Operation(summary = "Get all purchased")
    @GetMapping("/all-purchased")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Set<PurchasedServiceDTO>> getAllPurchased(HttpServletRequest request) {
        return service.getAllPurchased(request);
    }

    @Operation(summary = "Create schedule for Hourly Service")
    @PostMapping("/create/hourly")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<String> createHourlySchedule(
            HttpServletRequest request, @Valid @RequestBody HourlyScheduleDTO hourlyScheduleDTO
    ) {
        return service.createHourlySchedule(request, hourlyScheduleDTO);
    }

    @Operation(summary = "Create schedule for Return Service")
    @PostMapping("/create/return")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<String> createReturnSchedule(
            HttpServletRequest request, @Valid @RequestBody ReturnScheduleDTO returnScheduleDTO
    ) {
        return service.createReturnSchedule(request, returnScheduleDTO);
    }

    @Operation(summary = "Create schedule for Delivery Service")
    @PostMapping("/create/delivery")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<String> createDeliverySchedule(
            HttpServletRequest request, @Valid @RequestBody DeliveryScheduleDTO deliveryScheduleDTO
    ) {
        return service.createDeliverySchedule(request, deliveryScheduleDTO);
    }

}

