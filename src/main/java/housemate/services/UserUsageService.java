/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.services;

import housemate.entities.Order;
import housemate.entities.OrderItem;
import housemate.entities.PackageServiceItem;
import housemate.entities.Service;
import housemate.entities.UserUsage;
import housemate.models.responses.MyPurchasedResponse;
import housemate.models.responses.UserUsageResponse;
import housemate.repositories.OrderItemRepository;
import housemate.repositories.OrderRepository;
import housemate.repositories.PackageServiceItemRepository;
import housemate.repositories.ServiceRepository;
import housemate.repositories.UserUsageRepository;
import housemate.utils.AuthorizationUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author ThanhF
 */
@org.springframework.stereotype.Service
public class UserUsageService {

    @Autowired
    private AuthorizationUtil authorizationUtil;

    @Autowired
    private UserUsageRepository userUsageRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private PackageServiceItemRepository packageServiceItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    public ResponseEntity<List<UserUsageResponse>> getAllUserUsageForSchedule(HttpServletRequest request) {
        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);

        Map<Integer, UserUsageResponse> mapUserUsageResponse = new HashMap<>();

        List<UserUsage> listUserUsage = userUsageRepository.getAllUserUsageByUserIdAndNotExpired(userId);
        for (UserUsage userUsage : listUserUsage) {

            if (userUsage.getEndDate().compareTo(LocalDateTime.now()) < 0) {
                userUsage.setExpired(true);
                userUsageRepository.save(userUsage);
                continue;
            }

            int serviceId = userUsage.getServiceId();

            int orderItemId = userUsage.getOrderItemId();
            OrderItem orderItem = orderItemRepository.findById(orderItemId);

            Service service = serviceRepository.getServiceByServiceId(serviceId);

            UserUsageResponse userUsageResponse = mapUserUsageResponse.get(serviceId);
            if (userUsageResponse == null) {
                userUsageResponse = new UserUsageResponse();
                userUsageResponse.setService(service);
                userUsageResponse.setListUserUsage(new ArrayList<>());
                mapUserUsageResponse.put(service.getServiceId(), userUsageResponse);
            }

            int currentTotal = userUsageResponse.getTotal();
            userUsageResponse.setTotal(currentTotal + userUsage.getTotal());

            int currentRemaining = userUsageResponse.getRemaining();
            userUsageResponse.setRemaining(currentRemaining + userUsage.getRemaining());

            Service serviceChild = serviceRepository.getServiceByServiceId(orderItem.getServiceId());
            userUsage.setService(serviceChild);
            userUsageResponse.getListUserUsage().add(userUsage);
        }

        List<UserUsageResponse> listUserUsageResponse = new ArrayList<>();
        for (UserUsageResponse value : mapUserUsageResponse.values()) {
            listUserUsageResponse.add(value);
        }

        return ResponseEntity.status(HttpStatus.OK).body(listUserUsageResponse);
    }

    public ResponseEntity<?> getUserUsageByOrderItemId(HttpServletRequest request, int orderItemId) {

        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        OrderItem orderItem = orderItemRepository.findById(orderItemId);
        if (orderItem == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order Item not found");
        }

        LocalDateTime startDate = null;
        int total = 0;
        int totalRemaining = 0;

        UserUsageResponse userUsageResponse = new UserUsageResponse();
        List<UserUsage> listUserUsage = userUsageRepository.getAllUserUsageByOrderItemIdAndUserIdAndNotExpired(orderItemId, userId);
        for (UserUsage userUsage : listUserUsage) {
            if (userUsage.getEndDate().compareTo(LocalDateTime.now()) < 0) {
                userUsage.setExpired(true);
                userUsageRepository.save(userUsage);
            }
            Service service = serviceRepository.getServiceByServiceId(userUsage.getServiceId());
            userUsage.setService(service);

            startDate = userUsage.getStartDate();
            total += userUsage.getTotal();
            totalRemaining += userUsage.getRemaining();

        }

//        listUserUsage = userUsageRepository.getAllUserUsageByOrderItemIdAndUserIdAndNotExpired(orderItemId, userId); //comment lại để xét logic
        userUsageResponse.setListUserUsage(listUserUsage);

        Service service = serviceRepository.getServiceByServiceId(orderItem.getServiceId());
        userUsageResponse.setService(service);

        userUsageResponse.setStartDate(startDate);
        userUsageResponse.setEndDate(orderItem.getExpireDate());
        userUsageResponse.setTotal(total);
        userUsageResponse.setRemaining(totalRemaining);

        return ResponseEntity.status(HttpStatus.OK).body(userUsageResponse);
    }

    public ResponseEntity<List<MyPurchasedResponse>> getAllUserUsageForMyPurchased(HttpServletRequest request) {

        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);

        List<MyPurchasedResponse> listMyPurchasedResponse = new ArrayList<>();

        List<Order> listOrder = orderRepository.getAllOrderCompleteByUserId(userId);
        for (Order order : listOrder) {
            List<OrderItem> listOrderItem = orderItemRepository.getAllOrderItemByOrderId(order.getOrderId());
            for (OrderItem orderItem : listOrderItem) {

                List<String> listSingleServiceName = new ArrayList<>();

                MyPurchasedResponse myPurchasedResponse = new MyPurchasedResponse();

                Service service = serviceRepository.getServiceByServiceId(orderItem.getServiceId());
                if (service.isPackage()) {
                    List<PackageServiceItem> listPackageServiceItem = packageServiceItemRepository.findAllSingleServiceIdByPackageServiceId(service.getServiceId());
                    for (PackageServiceItem packageServiceItem : listPackageServiceItem) {
                        Service singleService = serviceRepository.getServiceByServiceId(packageServiceItem.getSingleServiceId());
                        listSingleServiceName.add(singleService.getTitleName());
                    }
                } else {
                    listSingleServiceName.add(service.getTitleName());
                }
                myPurchasedResponse.setEndDate(orderItem.getExpireDate());
                myPurchasedResponse.setStartDate(orderItem.getExpireDate());
                myPurchasedResponse.setSingleServiceName(listSingleServiceName);
                myPurchasedResponse.setService(service);
                listMyPurchasedResponse.add(myPurchasedResponse);
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(listMyPurchasedResponse);

    }
}
