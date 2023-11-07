/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.controllers;

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

    @GetMapping("/user/days-ago/{daysAgo}")
    public ResponseEntity<?> getAnalyticUser(HttpServletRequest request, @PathVariable int daysAgo) {
        return analyticService.getAnalyticUser(request, daysAgo);
    }

    @GetMapping("/page/days-ago/{daysAgo}")
    public ResponseEntity<?> getAnalyticPage(HttpServletRequest request, @PathVariable int daysAgo) {
        return analyticService.getAnalyticPage(request, daysAgo);
    }

}
