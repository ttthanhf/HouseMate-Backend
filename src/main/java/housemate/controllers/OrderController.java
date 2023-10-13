/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.controllers;

import housemate.models.CheckoutCreateDTO;
import housemate.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ThanhF
 */
@RestController
@RequestMapping("/order")
@CrossOrigin
@Tag(name = "Checkout")
public class OrderController {

    @Autowired
    OrderService orderService;

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create checkout")
    @PostMapping("/checkout")
    public ResponseEntity<?> createCheckout(HttpServletRequest request, @RequestBody CheckoutCreateDTO checkoutCreateDTO) {
        return orderService.createCheckout(request, checkoutCreateDTO);
    }
}
