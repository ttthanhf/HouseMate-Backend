/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.services;

import housemate.entities.Cart;
import housemate.entities.Order;
import housemate.entities.OrderItem;
import housemate.entities.Period;
import housemate.entities.UserAccount;
import housemate.models.CheckoutCreateDTO;
import housemate.repositories.CartRepository;
import housemate.repositories.OrderItemRepository;
import housemate.repositories.OrderRepository;
import housemate.repositories.PeriodRepository;
import housemate.repositories.UserRepository;
import housemate.utils.AuthorizationUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
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
public class OrderService {

    @Autowired
    private AuthorizationUtil authorizationUtil;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private PeriodRepository periodRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> createCheckout(HttpServletRequest request, CheckoutCreateDTO checkoutCreateDTO) {
        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        UserAccount user = userRepository.findByUserId(userId);
        int totalPrice = 0;

        //if order not exist or all order complete => create new order
        Order order = orderRepository.getOrderNotCompleteByUserId(userId);
        if (order == null) {
            order = new Order();
            order.setUserId(userId);
            order.setComplete(false);
            order.setDate(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
            order.setFullName(user.getFullName());
            order.setEmail(user.getEmailAddress());
            order.setPhone(user.getPhoneNumber());
            order.setAddress(user.getAddress());
            order.setPaymentMethod("");
            order = orderRepository.save(order);
        }

        //remove all order item
        orderItemRepository.removeAllOrderItemByUserIdAndOrderId(order.getOrderId());

        //save all order item
        List<OrderItem> listOrderItem = new ArrayList<>();
        List<Integer> listCartId = checkoutCreateDTO.getListCartId();
        for (int cartId : listCartId) {

            //if cart user not found => bad
            Cart cart = cartRepository.getCartById(cartId);
            if (cart == null || (cart.getUserId() != userId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Some cartId not found");
            }

            //create new order item
            OrderItem orderItem = new OrderItem();
            orderItem.setServiceId(cart.getServiceId());
            Period period = periodRepository.getPeriodByid(cart.getPeriodId());
            orderItem.setPeriodName(period.getPeriodName());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setOrderId(order.getOrderId());
            orderItem.setPrice(cart.getPrice());
            totalPrice += cart.getPrice();
            listOrderItem.add(orderItem);
        }

        listOrderItem = orderItemRepository.saveAll(listOrderItem);
        order.setListOrderItem(listOrderItem);
        order.setTotalPrice(totalPrice);
        order = orderRepository.save(order);
        return ResponseEntity.status(HttpStatus.OK).body(order);
    }

}