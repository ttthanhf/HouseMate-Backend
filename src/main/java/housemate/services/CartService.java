/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.services;

import housemate.entities.Cart;
import housemate.mappers.CartMapper;
import housemate.models.CartAddDTO;
import housemate.repositories.CartRepository;
import housemate.utils.AuthorizationUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 *
 * @author ThanhF
 */
@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AuthorizationUtil authorizationUtil;

    @Autowired
    private CartMapper cartMapper;

    public ResponseEntity<List<Cart>> getCart(HttpServletRequest request) {
        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        List<Cart> listCart = cartRepository.getCartByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(listCart);
    }

    public ResponseEntity<String> addToCart(HttpServletRequest request, CartAddDTO cartAdd) {
        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        int serviceId = cartAdd.getServiceId();
        if (cartRepository.getCartByUserIdAndServiceId(userId, serviceId) != null) {
            int quanlity = cartAdd.getQuanlity();
            cartRepository.updateCartQuanlity(userId, serviceId, quanlity);
            return ResponseEntity.status(HttpStatus.OK).body("Updated to cart");
        }
        cartAdd.setDate(LocalDateTime.now());
        cartAdd.setUserId(userId);
        Cart cart = cartMapper.mapToEntity(cartAdd);
        cartRepository.save(cart);
        return ResponseEntity.status(HttpStatus.OK).body("Added to cart");
    }

    public ResponseEntity<String> removeCart(HttpServletRequest request, int cartId) {
        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        if (cartRepository.deleteCart(userId, cartId) == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cart not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body("Removed");
    }
}
