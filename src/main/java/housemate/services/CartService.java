/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.services;

import housemate.entities.CartItem;
import housemate.entities.Period;
import housemate.entities.Service;
import housemate.models.CartDTO;
import housemate.models.CartItemDTO;
import housemate.repositories.CartItemRepository;
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
    private CartItemRepository cartItemRepository;

    @Autowired
    private PeriodRepository periodRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private AuthorizationUtil authorizationUtil;

    public ResponseEntity<CartDTO> getCart(HttpServletRequest request) {

        int subTotal = 0;
        int finalTotal = 0;

        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);

        CartDTO cartDTO = new CartDTO();

        List<CartItem> listCartItem = cartItemRepository.getAllCartItemByUserId(userId);

        for (CartItem cartItem : listCartItem) {
            Service service = serviceRepository.getServiceByServiceId(cartItem.getServiceId());

            Period period = periodRepository.getPeriodByid(cartItem.getPeriodId());
            subTotal += service.getOriginalPrice() * cartItem.getQuantity() * period.getPercent();

            finalTotal += service.getFinalPrice() * cartItem.getQuantity() * period.getPercent();

            List<Period> listPeriod = periodRepository.findAll();
            for (Period periodItem : listPeriod) {

                int finalPrice = (int) (periodItem.getPercent() * service.getFinalPrice());
                periodItem.setFinalPrice(finalPrice);

                int originalPrice = (int) (periodItem.getPercent() * service.getOriginalPrice());
                periodItem.setOriginalPrice(originalPrice);
            }
            service.setListPeriodPrice(listPeriod);
            cartItem.setService(service);
        }

        cartDTO.setUserId(userId);
        cartDTO.setListCartItem(listCartItem);
        cartDTO.setDiscountPrice(subTotal - finalTotal);
        cartDTO.setFinalTotalPrice(finalTotal);
        cartDTO.setSubTotal(subTotal);

        return ResponseEntity.status(HttpStatus.OK).body(cartDTO);
    }

    public ResponseEntity<String> addToCart(HttpServletRequest request, CartItemDTO cartItemDTO) {

        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        int serviceId = cartItemDTO.getServiceId();

        Service service = serviceRepository.getServiceByServiceId(serviceId);
        if (service == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Service not found");
        }
        //if have sale price => servicePrice = sale price
        int servicePrice = serviceRepository.getFinalPriceByServiceId(serviceId);

        //if have item in cart -> update quantity and price only
        if (cartItemRepository.getCartByUserIdAndServiceId(userId, serviceId) != null) {

            CartItem cartItem = cartItemRepository.getCartByUserIdAndServiceId(userId, serviceId);

            int quantity = cartItem.getQuantity() + cartItemDTO.getQuantity();

            //if quantity set > 9999 => bad request
            if (quantity > 9999) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Maxium quanity one item in cart is 9999");
            }

            int periodIdCartDTO = cartItemDTO.getPeriodId();
            int periodId = cartItem.getPeriodId();
            if (periodId != periodIdCartDTO) {
                periodId = periodIdCartDTO;
            }
            Float percent = periodRepository.getPeriodByid(periodId).getPercent();
            int pricePerQuantity = (int) (servicePrice * percent);
            int price = pricePerQuantity * quantity;

            cartItemRepository.updateCart(userId, serviceId, quantity, price, periodId);
            return ResponseEntity.status(HttpStatus.OK).body("Added to cart");
        }

        //if dont have item in cart -> create new item in cart
        int quantity = cartItemDTO.getQuantity();
        int periodId = cartItemDTO.getPeriodId();
        Float percent = periodRepository.getPeriodByid(periodId).getPercent();
        int price = (int) (servicePrice * percent * quantity);

        CartItem cartItem = new CartItem();
        cartItem.setUserId(userId);
        cartItem.setServiceId(serviceId);
        cartItem.setPeriodId(periodId);
        cartItem.setQuantity(quantity);
        cartItem.setPrice(price);
        cartItemRepository.save(cartItem);

        return ResponseEntity.status(HttpStatus.OK).body("Added to cart");
    }

    public ResponseEntity<String> updateToCart(HttpServletRequest request, CartItemDTO cartItemDTO) {

        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        int serviceId = cartItemDTO.getServiceId();

        //if service not found => NOT_FOUND
        Service service = serviceRepository.getServiceByServiceId(serviceId);
        if (service == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Service not found");
        }

        //if servicer not exist in cart => NOT_FOUND
        if (cartItemRepository.getCartByUserIdAndServiceId(userId, serviceId) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Service not exist in cart");
        }

        //check period id exist or not
        int periodId = cartItemDTO.getPeriodId();
        Period period = periodRepository.getPeriodByid(periodId);
        if (period == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Period id not found");
        }

        //if quantity set > 9999 => bad request
        int quantity = cartItemDTO.getQuantity();
        if (quantity > 9999) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Maxium quanity one item in cart is 9999");
        }

        //if have sale price => servicePrice = sale price
        int servicePrice = serviceRepository.getFinalPriceByServiceId(serviceId);
        Float percent = period.getPercent();
        int price = (int) (servicePrice * quantity * percent);

        cartItemRepository.updateCart(userId, serviceId, quantity, price, periodId);
        return ResponseEntity.status(HttpStatus.OK).body("Updated to cart");
    }

    public ResponseEntity<String> removeCart(HttpServletRequest request, int cartId) {
        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        if (cartItemRepository.deleteCart(userId, cartId) == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cart not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body("Removed");
    }

    public ResponseEntity<String> removeAllCartByUserId(HttpServletRequest request) {
        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        if (cartItemRepository.deleteAllCartByUserId(userId) == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cart not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body("Removed All Cart");
    }

    public void updateAllCartPriceWhenPeriodIdChange(int periodId, Float percent) {
        List<Integer> listCartId = cartItemRepository.getAllCartIdByPeriodId(periodId);

        //update all cart item price when period id change
        for (int cartId : listCartId) {
            CartItem cartItem = cartItemRepository.getCartById(cartId);
            int serviceId = cartItem.getServiceId();

            //if have sale price => servicePrice = sale price
            int servicePrice = serviceRepository.getOriginalPriceByServiceId(serviceId);
            int finalPrice = serviceRepository.getFinalPriceByServiceId(serviceId);
            if (finalPrice != 0) {
                servicePrice = finalPrice;
            }

            int serviceQuantity = cartItem.getQuantity();
            int price = (int) (servicePrice * serviceQuantity * percent);
            cartItemRepository.updateCartPriceByCartId(cartItem.getCartId(), price);
        }
    }
}
