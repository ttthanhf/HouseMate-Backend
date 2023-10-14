/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.controllers;

import housemate.entities.Period;
import housemate.services.PeriodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author ThanhF
 */
@RestController
@RequestMapping("/period")
@CrossOrigin
@Tag(name = "Period")
public class PeriodController {

    @Autowired
    PeriodService periodService;

    @Operation(summary = "Get all period item")
    @GetMapping("/all")
    public ResponseEntity<List<Period>> getAllPeriod() {
        return periodService.getAllPeriod();
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update percent period (require admin role)")
    @PutMapping("/update/{periodId}/{percent}")
    public ResponseEntity<String> getUpdatePeriod(HttpServletRequest request, @PathVariable int periodId, @PathVariable Float percent) {
        return periodService.updatePeriodPercentByPeriodId(request, periodId, percent);
    }
}
