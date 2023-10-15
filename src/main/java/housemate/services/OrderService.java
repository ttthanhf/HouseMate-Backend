/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.services;

import housemate.entities.CartItem;
import housemate.entities.Order;
import housemate.entities.OrderItem;
import housemate.entities.Period;
import housemate.entities.Service;
import housemate.entities.UserAccount;
import housemate.models.CheckoutCreateDTO;
import housemate.repositories.CartItemRepository;
import housemate.repositories.OrderItemRepository;
import housemate.repositories.OrderRepository;
import housemate.repositories.PeriodRepository;
import housemate.repositories.ServiceRepository;
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

/**
 *
 * @author ThanhF
 */
@org.springframework.stereotype.Service
public class OrderService {

    @Autowired
    private AuthorizationUtil authorizationUtil;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private PeriodRepository periodRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<List<Order>> getAllOrderComplete(HttpServletRequest request) {
        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        List<Order> listOrder = orderRepository.getAllOrderCompleteByUserId(userId);
        for (Order order : listOrder) {
            List<OrderItem> listOrderItem = orderItemRepository.getAllOrderItemByOrderId(order.getOrderId());
            order.setListOrderItem(listOrderItem);
        }
        return ResponseEntity.status(HttpStatus.OK).body(listOrder);
    }

    public ResponseEntity<?> getOrderNotComplete(HttpServletRequest request) {
        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        Order order = orderRepository.getOrderNotCompleteByUserId(userId);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No any order have been created or in not complete");
        }
        List<OrderItem> listOrderItem = orderItemRepository.getAllOrderItemByOrderId(order.getOrderId());
        for (OrderItem orderItem : listOrderItem) {
            Service service = serviceRepository.getServiceByServiceId(orderItem.getServiceId());
            orderItem.setService(service);
        }
        order.setListOrderItem(listOrderItem);
        return ResponseEntity.status(HttpStatus.OK).body(order);
    }

    public ResponseEntity<String> createCheckout(HttpServletRequest request, CheckoutCreateDTO checkoutCreateDTO) {
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
            order.setFullName(user.getFullName() != null ? user.getFullName() : "");
            order.setEmail(user.getEmailAddress() != null ? user.getEmailAddress() : "");
            order.setPhone(user.getPhoneNumber() != null ? user.getPhoneNumber() : "");
            order.setAddress(user.getAddress() != null ? user.getAddress() : "");
            order.setPaymentMethod("");
            order = orderRepository.save(order);
        }

        //update newset time
        order.setDate(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));

        //remove all order item
        orderItemRepository.removeAllOrderItemByUserIdAndOrderId(order.getOrderId());

        //save all order item
        List<OrderItem> listOrderItem = new ArrayList<>();
        List<Integer> listCartId = checkoutCreateDTO.getListCartId();
        for (int cartId : listCartId) {

            //if cart user not found => bad
            CartItem cartItem = cartItemRepository.getCartById(cartId);
            if (cartItem == null || (cartItem.getUserId() != userId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Some cartId not found");
            }

            //create new order item
            OrderItem orderItem = new OrderItem();
            orderItem.setServiceId(cartItem.getServiceId());
            Period period = periodRepository.getPeriodByid(cartItem.getPeriodId());
            orderItem.setPeriodName(period.getPeriodName());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setOrderId(order.getOrderId());
            orderItem.setPrice(cartItem.getPrice());
            totalPrice += cartItem.getPrice();
            listOrderItem.add(orderItem);
        }

        listOrderItem = orderItemRepository.saveAll(listOrderItem);
        order.setListOrderItem(listOrderItem);
        order.setTotalPrice(totalPrice);
        order = orderRepository.save(order);
        return ResponseEntity.status(HttpStatus.OK).body("Order created");
    }

}
