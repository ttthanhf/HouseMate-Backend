/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.controllers;

import housemate.entities.Cart;
import housemate.models.CartAddDTO;
import housemate.services.CartService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ThanhF
 */
@RestController
@RequestMapping("/cart")
@CrossOrigin
public class CartController {

    @Autowired
    CartService cartService;

    @GetMapping("/")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<Cart>> getCart(HttpServletRequest request) {
        return cartService.getCart(request);
    }

    @PostMapping("/")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<String> addCart(HttpServletRequest request, @RequestBody CartAddDTO cartAdd) {
        return cartService.addToCart(request, cartAdd);
    }

    @DeleteMapping("/{cartId}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<String> removeCart(HttpServletRequest request, @Valid @PathVariable int cartId) {
        return cartService.removeCart(request, cartId);
    }
}
