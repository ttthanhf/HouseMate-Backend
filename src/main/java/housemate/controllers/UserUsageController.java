/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.controllers;

import housemate.models.responses.MyPurchasedResponse;
import housemate.models.responses.UserUsageResponse;
import housemate.services.UserUsageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ThanhF
 */
@RestController
@RequestMapping("/user-usage")
@CrossOrigin
@Tag(name = "User Usage")
@SecurityRequirement(name = "bearerAuth")
public class UserUsageController {

    @Autowired
    private UserUsageService userUsageService;

    @Operation(summary = "Get all User Usage")
    @GetMapping("/schedule")
    public ResponseEntity<List<UserUsageResponse>> getAllUserUsageForSchedule(HttpServletRequest request) {
        return userUsageService.getAllUserUsageForSchedule(request);
    }

    @Operation(summary = "Get all User Usage for my purchased")
    @GetMapping("/my-purchased")
    public ResponseEntity<List<MyPurchasedResponse>> getAllUserUsageForMyPurchased(HttpServletRequest request) {
        return userUsageService.getAllUserUsageForMyPurchased(request);
    }

    @Operation(summary = "Get User Usage item for my purchased detail")
    @GetMapping("/{orderItemId}")
    public ResponseEntity<?> getUserUsageByOrderItemId(HttpServletRequest request, @PathVariable int orderItemId) {
        return userUsageService.getUserUsageByOrderItemId(request, orderItemId);
    }

}
