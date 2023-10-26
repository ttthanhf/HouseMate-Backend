/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.services;

import housemate.entities.Cart;
import housemate.entities.Order;
import housemate.entities.OrderItem;
import housemate.entities.Period;
import housemate.entities.Service;
import housemate.entities.UserAccount;
import housemate.models.CheckoutCreateDTO;
import housemate.repositories.CartRepository;
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
    private CartRepository cartRepository;
    
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
        UserAccount user = userRepository.findByUserId(userId);

        //setting for each order
        List<Order> listOrder = orderRepository.getAllOrderCompleteByUserId(userId);
        for (Order order : listOrder) {

            //setting for each OderItem
            List<OrderItem> listOrderItem = orderItemRepository.getAllOrderItemByOrderId(order.getOrderId());
            for (OrderItem orderItem : listOrderItem) {
                Service service = serviceRepository.getServiceByServiceId(orderItem.getServiceId());
                orderItem.setService(service);
                orderItem.setDiscountPrice(orderItem.getOriginalPrice() - orderItem.getFinalPrice());
            }
            order.setListOrderItem(listOrderItem);
            order.setDiscountPrice(order.getSubTotal() - order.getFinalPrice());
            order.setUser(user);
        }
        return ResponseEntity.status(HttpStatus.OK).body(listOrder);
    }
    
    public ResponseEntity<?> getOrderNotComplete(HttpServletRequest request) {
        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        UserAccount user = userRepository.findByUserId(userId);
        
        List<Cart> listCart = cartRepository.getAllCartByUserId(userId);
        if (listCart.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No any item in cart");
        }
        
        Order order = orderRepository.getOrderNotCompleteByUserId(userId);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No any order have been created or in not complete");
        }

        //setting for each orderItem
        List<OrderItem> listOrderItem = orderItemRepository.getAllOrderItemByOrderId(order.getOrderId());
        for (OrderItem orderItem : listOrderItem) {
            Service service = serviceRepository.getServiceByServiceId(orderItem.getServiceId());
            orderItem.setService(service);
            orderItem.setDiscountPrice(orderItem.getOriginalPrice() - orderItem.getFinalPrice());
        }

        //if addr = null => addr = ""
        if (user.getAddress() == null) {
            user.setAddress("");
        }
        
        if (user.getPhoneNumber() == null) {
            user.setPhoneNumber("");
        }
        
        order.setUser(user);
        order.setDiscountPrice(order.getSubTotal() - order.getFinalPrice());
        order.setListOrderItem(listOrderItem);
        
        return ResponseEntity.status(HttpStatus.OK).body(order);
    }
    
    public ResponseEntity<String> createCheckout(HttpServletRequest request, CheckoutCreateDTO checkoutCreateDTO) {
        
        int finalPrice = 0;
        int subTotal = 0;
        
        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        UserAccount user = userRepository.findByUserId(userId);

        //if order not exist or all order complete => create new order
        Order order = orderRepository.getOrderNotCompleteByUserId(userId);
        if (order == null) {
            
            order = new Order();
            order.setUserId(userId);
            order.setComplete(false);
            
            order = orderRepository.save(order);
        }
        
        order.setDate(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));

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
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setOrderId(order.getOrderId());
            
            Period period = periodRepository.getPeriodById(cart.getPeriodId());
            orderItem.setCreateDate(LocalDateTime.now());
            orderItem.setExpireDate(LocalDateTime.now().plusMonths(period.getPeriodValue()));
            orderItem.setPeriodName(period.getPeriodValue() + " " + period.getPeriodName());
            
            int quantity = cart.getQuantity();
            
            int finalPriceCart = (int) (quantity * period.getFinalPrice());
            orderItem.setFinalPrice(finalPriceCart);
            
            finalPrice += finalPriceCart;
            
            int originalPriceCart = (int) (quantity * period.getOriginalPrice());
            orderItem.setOriginalPrice(originalPriceCart);
            
            subTotal += originalPriceCart;
            
            listOrderItem.add(orderItem);
        }

        //remove all order item
        orderItemRepository.removeAllOrderItemByOrderId(order.getOrderId());
        listOrderItem = orderItemRepository.saveAll(listOrderItem);
        
        order.setUser(user);
        
        order.setFinalPrice(finalPrice);
        order.setSubTotal(subTotal);
        order = orderRepository.save(order);
        
        order.setListOrderItem(listOrderItem);
        
        return ResponseEntity.status(HttpStatus.OK).body("Order created");
    }
    
}
