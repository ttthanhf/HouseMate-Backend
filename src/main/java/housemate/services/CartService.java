/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.services;

import housemate.entities.Cart;
import housemate.entities.Service;
import housemate.mappers.CartMapper;
import housemate.models.CartAddDTO;
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

    @Autowired
    private CartMapper cartMapper;

    public ResponseEntity<List<Cart>> getCart(HttpServletRequest request) {

        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        List<Cart> listCart = cartRepository.getCartByUserId(userId);

        //set service item in cart
        for (Cart cart : listCart) {
            cart.setService(serviceRepository.getServiceByServiceId(cart.getServiceId()));
        }

        return ResponseEntity.status(HttpStatus.OK).body(listCart);
    }

    public ResponseEntity<String> addToCart(HttpServletRequest request, CartAddDTO cartAdd) {

        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        int serviceId = cartAdd.getServiceId();

        Service service = serviceRepository.getServiceByServiceId(serviceId);
        if (service == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Service not found");
        }
        //if have sale price => servicePrice = sale price
        int servicePrice = service.getOriginalPrice();
        int salePrice = serviceRepository.getSalePriceByServiceId(serviceId);
        if (salePrice != 0) {
            servicePrice = salePrice;
        }
        int periodId = cartAdd.getPeriodId();

        //check period id exist or not
        Float percent = periodRepository.getPeriodByid(periodId).getPercent();
        if (percent == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("PeriodId not found");
        }
        int price = (int) (servicePrice * cartAdd.getQuantity() * percent);

        //if have item in cart -> update quantity only
        if (cartRepository.getCartByUserIdAndServiceId(userId, serviceId) != null) {
            int quanlity = cartAdd.getQuantity();
            cartRepository.updateCartQuantity(userId, serviceId, quanlity, price, periodId);
            return ResponseEntity.status(HttpStatus.OK).body("Updated to cart");
        }

        //if dont have item in cart -> create new item in cart
        cartAdd.setUserId(userId);
        Cart cart = cartMapper.mapToEntity(cartAdd);
        cart.setPrice(price);

        cartRepository.save(cart);
        return ResponseEntity.status(HttpStatus.OK).body("Added to cart");
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
            int salePrice = serviceRepository.getSalePriceByServiceId(serviceId);
            if (salePrice != 0) {
                servicePrice = salePrice;
            }

            int serviceQuantity = cart.getQuantity();
            int price = (int) (servicePrice * serviceQuantity * percent);
            cartRepository.updateCartPriceByCartId(cart.getCartId(), price);
        }
    }
}
