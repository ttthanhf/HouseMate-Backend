package housemate.services;

import housemate.entities.Schedule;
import housemate.entities.Service;
import housemate.mappers.ScheduleMapper;
import housemate.models.*;
import housemate.repositories.OrderItemRepository;
import housemate.repositories.OrderRepository;
import housemate.repositories.ScheduleRepository;
import housemate.repositories.ServiceRepository;
import housemate.utils.AuthorizationUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author ThanhF
 */
@org.springframework.stereotype.Service
public class ScheduleService {

    ServiceRepository serviceRepository;
    ScheduleRepository scheduleRepository;
    ScheduleMapper scheduleMapper;
    AuthorizationUtil authorizationUtil;
    OrderRepository orderRepository;
    OrderItemRepository orderItemRepository;
    final int FIND_STAFF_HOURS = 3;

    @Autowired
    public ScheduleService(
            ServiceRepository serviceRepository,
            ScheduleRepository scheduleRepository,
            ScheduleMapper scheduleMapper,
            AuthorizationUtil authorizationUtil,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository
    ) {
        this.serviceRepository = serviceRepository;
        this.scheduleRepository = scheduleRepository;
        this.scheduleMapper = scheduleMapper;
        this.authorizationUtil = authorizationUtil;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public ResponseEntity<List<ScheduleEventDTO>> getScheduleForUser(HttpServletRequest request) {
        List<ScheduleEventDTO> events = new ArrayList<>();
        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);

        for (Schedule schedule : scheduleRepository.getByCustomerId(userId)) {
            ScheduleEventDTO eventDTO = new ScheduleEventDTO();

            Service service = serviceRepository.getServiceByServiceId(schedule.getServiceId());
            eventDTO.setTitle(service.getTitleName());
            eventDTO.setStart(schedule.getStartDate());
            eventDTO.setEnd(schedule.getEndDate());
            eventDTO.setStatus(schedule.getStatus());

            events.add(eventDTO);
        }

        return ResponseEntity.status(HttpStatus.OK).body(events);
    }


    public ResponseEntity<Schedule> getScheduleById(int scheduleId) {
        Schedule schedule = scheduleRepository.getByScheduleId(scheduleId);

        if (schedule == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        return ResponseEntity.status(HttpStatus.OK).body(schedule);
    }

    public ResponseEntity<Set<PurchasedServiceDTO>> getAllPurchased(HttpServletRequest request) {
        Set<PurchasedServiceDTO> services = new HashSet<>();
        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);

        // TODO: Get all serviceID based on order item
//        List<Order> orders = orderRepository.getAllOrderCompleteByUserId(userId);
//        for (Order order : orders) {
//            List<OrderItem> orderItems = orderItemRepository.getAllOrderItemByOrderId(order.getOrderId());
//
//            for (OrderItem orderItem : orderItems) {
//                // TODO: Check expiration
//                services.add(new PurchasedServiceDTO(orderItem.getServiceId()));
//
//            }
//        }

        // TODO: Get all type list

        return ResponseEntity.status(HttpStatus.OK).body(services);
    }

    public ResponseEntity<String> createHourlySchedule(HttpServletRequest request, HourlyScheduleDTO scheduleDTO) {
        // Validate service ID
        ResponseEntity<String> serviceIdValidation = validateServiceId(scheduleDTO.getServiceId());
        if (serviceIdValidation != null) return serviceIdValidation;

        // Validate start date
        LocalDateTime startDate = scheduleDTO.getDate().atTime(scheduleDTO.getTimeRanges().get(0));
        ResponseEntity<String> startDateValidation = validateDate(LocalDateTime.now(), startDate, FIND_STAFF_HOURS, "start time");
        if (startDateValidation != null) return startDateValidation;

        // Validate end date
        LocalDateTime endDate = scheduleDTO.getDate().atTime(scheduleDTO.getTimeRanges().get(1));
        ResponseEntity<String> endDateValidation = validateDate(startDate, endDate, 1, "end time");
        if (endDateValidation != null) return endDateValidation;

        // Store to database
        Schedule schedule = scheduleMapper.mapToEntity(scheduleDTO);
        int customerId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        schedule.setCustomerId(customerId);
        // TODO: Store orderItemId
        scheduleRepository.save(schedule);

        // TODO: Send notification to staff

        return ResponseEntity.status(HttpStatus.OK).body("Schedule create successfully! Please wait for our staff apply this job!");
    }

    public ResponseEntity<String> createReturnSchedule(HttpServletRequest request, ReturnScheduleDTO scheduleDTO) {
        final int MINIMUM_RETURN_HOUR = 4;

        // Validate service ID
        ResponseEntity<String> serviceIdValidation = validateServiceId(scheduleDTO.getServiceId());
        if (serviceIdValidation != null) return serviceIdValidation;

        // Validate pickupDate > current + 3hr
        LocalDateTime pickupDate = scheduleDTO.getPickupDate().atTime(scheduleDTO.getPickupTime());
        ResponseEntity<String> pickupDateValidation = validateDate(LocalDateTime.now(), pickupDate, FIND_STAFF_HOURS, "pickup date");
        if (pickupDateValidation != null) return pickupDateValidation;

        // Validate receivedDate > pickupDate + 4
        LocalDateTime receivedDate = scheduleDTO.getReceivedDate().atTime(scheduleDTO.getReceivedTime());
        ResponseEntity<String> receivedDateValidation = validateDate(pickupDate, receivedDate, MINIMUM_RETURN_HOUR, "received date");
        if (receivedDateValidation != null) return receivedDateValidation;

        // Store to database
        List<Schedule> schedules = scheduleMapper.mapToEntity(scheduleDTO);
        int customerId = authorizationUtil.getUserIdFromAuthorizationHeader(request);

        for (Schedule schedule: schedules) {
            schedule.setCustomerId(customerId);
            // TODO: Store orderItemId
            scheduleRepository.save(schedule);
        }

        // TODO: Send notification to staff

        return ResponseEntity.status(HttpStatus.OK).body("Schedule create successfully! Please wait for our staff apply this job!");
    }

    public ResponseEntity<String> createDeliverySchedule(HttpServletRequest request, DeliveryScheduleDTO scheduleDTO) {
        // Validate service ID
        ResponseEntity<String> serviceIdValidation = validateServiceId(scheduleDTO.getServiceId());
        if (serviceIdValidation != null) return serviceIdValidation;

        // Validate date > current + 3
        LocalDateTime date = scheduleDTO.getDate().atTime(scheduleDTO.getTime());
        ResponseEntity<String> receivedDateValidation = validateDate(LocalDateTime.now(), date, FIND_STAFF_HOURS, "date");
        if (receivedDateValidation != null) return receivedDateValidation;

        // TODO: Validate quantity in DTO is smaller than quantity in order

        // Store to database
        Schedule schedule = scheduleMapper.mapToEntity(scheduleDTO);
        int customerId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        schedule.setCustomerId(customerId);
        // TODO: Store orderItemId
        scheduleRepository.save(schedule);

        // TODO: Send notification to staff

        return ResponseEntity.status(HttpStatus.OK).body("Schedule create successfully! Please wait for our staff apply this job!");
    }

    private ResponseEntity<String> validateServiceId(int serviceId) {
        // Validate service ID
        Service service = serviceRepository.getServiceByServiceId(serviceId);
        if (service == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Can't find this service ID");
        }

        // TODO: Validate serviceId is in order

        // TODO: Check serviceID is correct type?

        return null;
    }


    private ResponseEntity<String> validateDate(LocalDateTime startDate, LocalDateTime endDate, int hours, String varName) {
        // Check time in office hours
        int scheduleHour = endDate.getHour();
        if (scheduleHour <= 7 || scheduleHour >= 20) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please set your " + varName + " in range from 7:00 to 20:00");
        }

        // Validate endDate > startDate + n hours
        if (!endDate.isAfter(startDate.plusHours(hours))) {
            String formattedDate = startDate.plusHours(hours).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You must set your " + varName + " after " + formattedDate);
        }

        return null;
    }

}
