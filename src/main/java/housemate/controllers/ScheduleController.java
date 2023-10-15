package housemate.controllers;

import housemate.models.ReturnScheduleDTO;
import housemate.models.DeliveryScheduleDTO;
import housemate.models.HourlyScheduleDTO;
import housemate.services.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@RequestMapping("/schedule")
@CrossOrigin
@Tag(name = "Schedule")
@SecurityRequirement(name = "bearerAuth")
public class ScheduleController {

    @Autowired
    ScheduleService service;

    @Operation(summary = "Create schedule for Hourly Service")
    @PostMapping("/create/hourly")
    public ResponseEntity<String> createHourlySchedule(@Valid @RequestBody HourlyScheduleDTO hourlyScheduleDTO) {
        return service.createHourlySchedule(hourlyScheduleDTO);
    }

    @Operation(summary = "Create schedule for Return Service")
    @PostMapping("/create/return")
    public ResponseEntity<String> createReturnSchedule(@Valid @RequestBody ReturnScheduleDTO returnScheduleDTO) {
        return service.createReturnSchedule(returnScheduleDTO);
    }

    @Operation(summary = "Create schedule for Delivery Service")
    @PostMapping("/create/delivery")
    public ResponseEntity<String> createDeliverySchedule(@Valid @RequestBody DeliveryScheduleDTO deliveryScheduleDTO) {
        return service.createDeliverySchedule(deliveryScheduleDTO);
    }

}

