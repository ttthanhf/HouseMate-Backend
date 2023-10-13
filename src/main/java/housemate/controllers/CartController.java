/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.controllers;

import housemate.entities.Cart;
import housemate.models.CartAddDTO;
import housemate.services.CartService;
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
@RequestMapping("/cart")
@CrossOrigin
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Cart")
public class CartController {

    @Autowired
    CartService cartService;

    @Operation(summary = "Get user cart by userId in token")
    @GetMapping("/")
    public ResponseEntity<List<Cart>> getCart(HttpServletRequest request) {
        return cartService.getCart(request);
    }

    @Operation(summary = "Add to cart. When cart already exist -> update quantity only")
    @PostMapping("/")
    public ResponseEntity<String> addCart(HttpServletRequest request, @RequestBody CartAddDTO cartAdd) {
        return cartService.addToCart(request, cartAdd);
    }

    @Operation(summary = "Detele cart when cart exist")
    @DeleteMapping("/{cartId}")
    public ResponseEntity<String> removeCart(HttpServletRequest request, @PathVariable int cartId) {
        return cartService.removeCart(request, cartId);
    }
}
