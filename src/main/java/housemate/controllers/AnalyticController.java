/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.controllers;

import housemate.models.AnalyticDTO;
import housemate.services.AnalyticService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author ThanhF
 */
@RestController
@RequestMapping("/analytics")
@CrossOrigin
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Analytics")
public class AnalyticController {

    @Autowired
    AnalyticService analyticService;

    @PostMapping("/user") //Dùng method get sẽ ko lấy được RequestBody nên phải dùng method POST
    public ResponseEntity<?> getAnalyticUser(HttpServletRequest request, @RequestBody AnalyticDTO analyticDTO) {
        return analyticService.getAnalyticUser(request, analyticDTO);
    }

    @PostMapping("/page")//Dùng method get sẽ ko lấy được RequestBody nên phải dùng method POST
    public ResponseEntity<?> getAnalyticPage(HttpServletRequest request, @RequestBody AnalyticDTO analyticDTO) {
        return analyticService.getAnalyticPage(request, analyticDTO);
    }

}
