package housemate.controllers;

import housemate.models.DeliveryScheduleDTO;
import housemate.models.HourlyScheduleDTO;
import housemate.models.ReturnScheduleDTO;
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

    @Operation(summary = "Get schedule")
    @GetMapping("/{userId}")
    public ResponseEntity<List<EventRes>> getScheduleByUserId(@PathVariable int userId) {
        return service.getScheduleByUserId(userId);
    }

    @Operation(summary = "Get all purchased service for the current user")
    @GetMapping("/all-purchased")
    public ResponseEntity<Set<PurchasedServiceRes>> getAllPurchased(HttpServletRequest request) {
        return service.getAllPurchased(request);
    }

    @Operation(summary = "Create schedule for Hourly Service")
    @PostMapping("/create/hourly")
    public ResponseEntity<String> createHourlySchedule(
            HttpServletRequest request, @Valid @RequestBody HourlyScheduleDTO hourlyScheduleDTO
    ) {
        return service.createHourlySchedule(request, hourlyScheduleDTO);
    }

    @Operation(summary = "Create schedule for Return Service")
    @PostMapping("/create/return")
    public ResponseEntity<String> createReturnSchedule(
            HttpServletRequest request, @Valid @RequestBody ReturnScheduleDTO returnScheduleDTO
    ) {
        return service.createReturnSchedule(request, returnScheduleDTO);
    }

    @Operation(summary = "Create schedule for Delivery Service")
    @PostMapping("/create/delivery")
    public ResponseEntity<String> createDeliverySchedule(
            HttpServletRequest request, @Valid @RequestBody DeliveryScheduleDTO deliveryScheduleDTO
    ) {
        return service.createDeliverySchedule(request, deliveryScheduleDTO);
    }

    @Operation(summary = "Update schedule for Hourly Service")
    @PutMapping("/update/hourly/{scheduleId}")
    public ResponseEntity<String> updateHourlySchedule(
            HttpServletRequest request, @Valid @RequestBody HourlyScheduleDTO hourlyScheduleDTO, @PathVariable int scheduleId
    ) {
        return service.updateHourlySchedule(request, hourlyScheduleDTO, scheduleId);
    }

    @Operation(summary = "Update schedule for Return Service")
    @PutMapping("/update/return/{scheduleId}")
    public ResponseEntity<String> updateReturnSchedule(
            HttpServletRequest request, @Valid @RequestBody ReturnScheduleDTO returnScheduleDTO, @PathVariable int scheduleId
    ) {
        return service.updateReturnSchedule(request, returnScheduleDTO, scheduleId);
    }

    @Operation(summary = "Update schedule for Delivery Service")
    @PutMapping("/update/delivery/{scheduleId}")
    public ResponseEntity<String> updateDeliverySchedule(
            HttpServletRequest request, @Valid @RequestBody DeliveryScheduleDTO deliveryScheduleDTO, @PathVariable int scheduleId
    ) {
        return service.updateDeliverySchedule(request, deliveryScheduleDTO, scheduleId);
    }

}

