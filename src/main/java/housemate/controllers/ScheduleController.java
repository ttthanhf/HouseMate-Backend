package housemate.controllers;

import housemate.entities.Schedule;
import housemate.constants.DeleteType;
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

    @Operation(summary = "Get all schedule for the current user")
    @GetMapping()
    public ResponseEntity<List<EventRes>> getScheduleForCurrentUser(HttpServletRequest request) {
        return service.getScheduleForCurrentUser(request);
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
    public ResponseEntity<?> createSchedule(HttpServletRequest request, @Valid @RequestBody ScheduleDTO scheduleDTO) {
        return service.createSchedule(request, scheduleDTO);
    }

    @Operation(summary = "Get schedule by schedule ID")
    @GetMapping("/{scheduleId}")
    public ResponseEntity<?> getScheduleById(HttpServletRequest request, @PathVariable int scheduleId) {
        return service.getScheduleById(request, scheduleId);
    }

    @Operation(summary = "Cancel schedule")
    @DeleteMapping("/cancel/{scheduleId}")
    public ResponseEntity<?> cancelSchedule(HttpServletRequest request, @PathVariable int scheduleId, DeleteType deleteType) {
        return service.cancelSchedule(request, scheduleId, deleteType);
    }

    @Operation(summary = "Update schedule")
    @PutMapping("/update/{scheduleId}")
    public ResponseEntity<?> updateSchedule(
            HttpServletRequest request, @Valid @RequestBody ScheduleUpdateDTO scheduleUpdateDTO, @PathVariable int scheduleId
    ) {
        return service.updateSchedule(request, scheduleUpdateDTO, scheduleId);
    }

}

