/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.services;

import housemate.entities.Cart;
import housemate.entities.Period;
import housemate.entities.Service;
import housemate.models.CartDTO;
import housemate.repositories.CartRepository;
import housemate.repositories.PeriodRepository;
import housemate.repositories.ServiceRepository;
import housemate.utils.AuthorizationUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author ThanhF
 */
@org.springframework.stereotype.Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private PeriodRepository periodRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private AuthorizationUtil authorizationUtil;

    public ResponseEntity<List<Cart>> getCart(HttpServletRequest request) {

        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);

        List<Cart> listCart = cartRepository.getAllCartByUserId(userId);

        for (Cart cart : listCart) {

            Service service = serviceRepository.getServiceByServiceId(cart.getServiceId());
            cart.setService(service);

            Period period = periodRepository.getPeriodByid(cart.getPeriodId());

            int finalPriceService = (int) (period.getPercent() * service.getFinalPrice());
            cart.setFinalPrice(finalPriceService);

            int originalPriceService = (int) (period.getPercent() * service.getOriginalPrice());
            cart.setOriginPrice(originalPriceService);

        }

        return ResponseEntity.status(HttpStatus.OK).body(listCart);
    }

    public ResponseEntity<String> addToCart(HttpServletRequest request, CartDTO carDTO) {

        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        int serviceId = carDTO.getServiceId();

        Service service = serviceRepository.getServiceByServiceId(serviceId);
        if (service == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Service not found");
        }

        //if have item in cart -> update quantity and price only
        if (cartRepository.getCartByUserIdAndServiceId(userId, serviceId) != null) {

            Cart cart = cartRepository.getCartByUserIdAndServiceId(userId, serviceId);

            int quantity = cart.getQuantity() + carDTO.getQuantity();

            //if quantity set > 9999 => bad request
            if (quantity > 9999) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Maxium quanity one item in cart is 9999");
            }

            int periodId = carDTO.getPeriodId();

            cartRepository.updateCart(userId, serviceId, quantity, periodId);
            return ResponseEntity.status(HttpStatus.OK).body("Added to cart");
        }

        //if dont have item in cart -> create new item in cart
        int quantity = carDTO.getQuantity();
        int periodId = carDTO.getPeriodId();

        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setServiceId(serviceId);
        cart.setPeriodId(periodId);
        cart.setQuantity(quantity);
        cartRepository.save(cart);

        return ResponseEntity.status(HttpStatus.OK).body("Added to cart");
    }

    public ResponseEntity<String> updateToCart(HttpServletRequest request, CartDTO cartDTO) {

        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        int serviceId = cartDTO.getServiceId();

        //if service not found => NOT_FOUND
        Service service = serviceRepository.getServiceByServiceId(serviceId);
        if (service == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Service not found");
        }

        //if servicer not exist in cart => NOT_FOUND
        if (cartRepository.getCartByUserIdAndServiceId(userId, serviceId) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Service not exist in cart");
        }

        //check period id exist or not
        int periodId = cartDTO.getPeriodId();
        Period period = periodRepository.getPeriodByid(periodId);
        if (period == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Period id not found");
        }

        //if quantity set > 9999 => bad request
        int quantity = cartDTO.getQuantity();
        if (quantity > 9999) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Maxium quanity one item in cart is 9999");
        }

        cartRepository.updateCart(userId, serviceId, quantity, periodId);
        return ResponseEntity.status(HttpStatus.OK).body("Updated to cart");
    }

    public ResponseEntity<String> removeCart(HttpServletRequest request, int cartId) {
        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        if (cartRepository.deleteCart(userId, cartId) == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cart not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body("Removed");
    }

    public ResponseEntity<String> removeAllCartByUserId(HttpServletRequest request) {
        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        if (cartRepository.deleteAllCartByUserId(userId) == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cart not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body("Removed All Cart");
    }
}
