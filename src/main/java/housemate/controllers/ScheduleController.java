package housemate.controllers;

import housemate.models.CleanlinessScheduleDTO;
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

    @Operation(summary = "Create schedule for HOURLY_GROUP")
    @PostMapping("/create/hourly")
    public ResponseEntity<String> createHourlySchedule(@Valid @RequestBody HourlyScheduleDTO hourlyScheduleDTO) {
        return service.createHourlySchedule(hourlyScheduleDTO);
    }

    @Operation(summary = "Create schedule for CLEANLINESS_GROUP")
    @PostMapping("/create/cleanliness")
    public ResponseEntity<String> createCleanlinessSchedule(@Valid @RequestBody CleanlinessScheduleDTO cleanlinessScheduleDTO) {
        return service.createCleanlinessSchedule(cleanlinessScheduleDTO);
    }

    @Operation(summary = "Create schedule for DELIVERY_GROUP")
    @PostMapping("/create/delivery")
    public ResponseEntity<String> createDeliverySchedule(@Valid @RequestBody DeliveryScheduleDTO deliveryScheduleDTO) {
        return service.createDeliverySchedule(deliveryScheduleDTO);
    }

}

