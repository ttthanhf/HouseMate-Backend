/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.controllers;

import housemate.entities.Order;
import housemate.models.CheckoutCreateDTO;
import housemate.services.OrderService;
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
@RequestMapping("/order")
@CrossOrigin
@Tag(name = "Checkout")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    @Autowired
    OrderService orderService;

    @Operation(summary = "Create checkout")
    @PostMapping("/checkout/create")
    public ResponseEntity<String> createCheckout(HttpServletRequest request, @RequestBody CheckoutCreateDTO checkoutCreateDTO) {
        return orderService.createCheckout(request, checkoutCreateDTO);
    }

    @Operation(summary = "Get all Order complete")
    @GetMapping("/complete")
    public ResponseEntity<List<Order>> getAllOrderComplete(HttpServletRequest request) {
        return orderService.getAllOrderComplete(request);
    }

    @Operation(summary = "Get Order not complete")
    @GetMapping("/checkout")
    public ResponseEntity<?> getOrderNotComplete(HttpServletRequest request) {
        return orderService.getOrderNotComplete(request);
    }
}
