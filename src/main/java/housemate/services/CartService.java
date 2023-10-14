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
        List<Cart> listCart = cartRepository.getCartByUserId(userId);

        //set service item in cart
        for (Cart cart : listCart) {
            cart.setService(serviceRepository.getServiceByServiceId(cart.getServiceId()));
        }

        return ResponseEntity.status(HttpStatus.OK).body(listCart);
    }

    public ResponseEntity<String> addToCart(HttpServletRequest request, CartDTO cartDTO) {

        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        int serviceId = cartDTO.getUserId();

        Service service = serviceRepository.getServiceByServiceId(serviceId);
        if (service == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Service not found");
        }
        //if have sale price => servicePrice = sale price
        int servicePrice = service.getOriginalPrice();
        int finalPrice = serviceRepository.getFinalPriceByServiceId(serviceId);
        if (finalPrice != 0) {
            servicePrice = finalPrice;
        }

        //if have item in cart -> update quantity and price only
        if (cartRepository.getCartByUserIdAndServiceId(userId, serviceId) != null) {

            Cart cart = cartRepository.getCartByUserIdAndServiceId(userId, serviceId);

            int quantity = cart.getQuantity() + cartDTO.getQuantity();

            int periodIdCartDTO = cartDTO.getPeriodId();
            int periodId = cart.getPeriodId();
            if (periodId != periodIdCartDTO) {
                periodId = periodIdCartDTO;
            }
            Float percent = periodRepository.getPeriodByid(periodId).getPercent();
            int pricePerQuantity = (int) (servicePrice * percent);
            int price = pricePerQuantity * quantity;

            cartRepository.updateCart(userId, serviceId, quantity, price, periodId);
            return ResponseEntity.status(HttpStatus.OK).body("Added to cart");
        }

        int periodId_default = 1;// default for add to cart
        Float percent = periodRepository.getPeriodByid(periodId_default).getPercent();
        int pricePerQuantity = (int) (servicePrice * percent);

        //if dont have item in cart -> create new item in cart
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setServiceId(serviceId);
        cart.setPeriodId(periodId_default);
        cart.setQuantity(1); // default for add to cart
        cart.setPrice(pricePerQuantity);

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
        Float percent = period.getPercent();

        int quantity = cartDTO.getQuantity();

        //if have sale price => servicePrice = sale price
        int servicePrice = service.getOriginalPrice();
        int finalPrice = serviceRepository.getFinalPriceByServiceId(serviceId);
        if (finalPrice != 0) {
            servicePrice = finalPrice;
        }
        int price = (int) (servicePrice * quantity * percent);

        cartRepository.updateCart(userId, serviceId, quantity, price, periodId);
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

    public void updateAllCartPriceWhenPeriodIdChange(int periodId, Float percent) {
        List<Integer> listCartId = cartRepository.getAllCartIdByPeriodId(periodId);

        //update all cart item price when period id change
        for (int cartId : listCartId) {
            Cart cart = cartRepository.getCartById(cartId);
            int serviceId = cart.getServiceId();

            //if have sale price => servicePrice = sale price
            int servicePrice = serviceRepository.getOriginalPriceByServiceId(serviceId);
            int finalPrice = serviceRepository.getFinalPriceByServiceId(serviceId);
            if (finalPrice != 0) {
                servicePrice = finalPrice;
            }

            int serviceQuantity = cart.getQuantity();
            int price = (int) (servicePrice * serviceQuantity * percent);
            cartRepository.updateCartPriceByCartId(cart.getCartId(), price);
        }
    }
}
