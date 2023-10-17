package housemate.services;

import housemate.constants.Cycle;
import housemate.entities.*;
import housemate.mappers.ScheduleMapper;
import housemate.models.*;
import housemate.repositories.*;
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

    final int FIND_STAFF_HOURS = 3;
    ServiceRepository serviceRepository;
    ScheduleRepository scheduleRepository;
    ScheduleMapper scheduleMapper;
    AuthorizationUtil authorizationUtil;
    OrderRepository orderRepository;
    OrderItemRepository orderItemRepository;
    ServiceTypeRepository serviceTypeRepository;

    @Autowired
    public ScheduleService(
            ServiceRepository serviceRepository,
            ScheduleRepository scheduleRepository,
            ScheduleMapper scheduleMapper,
            AuthorizationUtil authorizationUtil,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            ServiceTypeRepository serviceTypeRepository
    ) {
        this.serviceRepository = serviceRepository;
        this.scheduleRepository = scheduleRepository;
        this.scheduleMapper = scheduleMapper;
        this.authorizationUtil = authorizationUtil;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.serviceTypeRepository = serviceTypeRepository;
    }

    public ResponseEntity<List<ScheduleEventDTO>> getScheduleForUser(HttpServletRequest request) {
        final int DEFAULT_CYCLE = 4;
        List<ScheduleEventDTO> events = new ArrayList<>();
        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);

        for (Schedule schedule : scheduleRepository.getByCustomerId(userId)) {
            if (schedule.getCycle() == Cycle.ONLY_ONE_TIME) {
                ScheduleEventDTO eventDTO = new ScheduleEventDTO();

                Service service = serviceRepository.getServiceByServiceId(schedule.getServiceId());
                eventDTO.setTitle(service.getTitleName());
                eventDTO.setStart(schedule.getStartDate());
                eventDTO.setEnd(schedule.getEndDate());
                eventDTO.setStatus(schedule.getStatus());

                events.add(eventDTO);
                // TODO: Check expire date
            } else if (schedule.getCycle() == Cycle.EVERY_WEEK) {
                for (int i = 0; i < DEFAULT_CYCLE; i++) {
                    ScheduleEventDTO eventDTO = new ScheduleEventDTO();

                    Service service = serviceRepository.getServiceByServiceId(schedule.getServiceId());
                    eventDTO.setTitle(service.getTitleName());
                    eventDTO.setStart(schedule.getStartDate().plusWeeks(i));
                    eventDTO.setEnd(schedule.getEndDate().plusWeeks(i));
                    eventDTO.setStatus(schedule.getStatus());

                    events.add(eventDTO);
                }

            } else if (schedule.getCycle() == Cycle.EVERY_MONTH) {
                for (int i = 0; i < DEFAULT_CYCLE; i++) {
                    ScheduleEventDTO eventDTO = new ScheduleEventDTO();

                    Service service = serviceRepository.getServiceByServiceId(schedule.getServiceId());
                    eventDTO.setTitle(service.getTitleName());
                    eventDTO.setStart(schedule.getStartDate().plusMonths(i));
                    eventDTO.setEnd(schedule.getEndDate().plusMonths(i));
                    eventDTO.setStatus(schedule.getStatus());

                    events.add(eventDTO);
                }
            }

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
        Set<PurchasedServiceDTO> purchases = new HashSet<>();
        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);

        // Get all serviceID based on order item
        List<Order> orders = orderRepository.getAllOrderCompleteByUserId(userId);
        for (Order order : orders) {
            List<OrderItem> orderItems = orderItemRepository.getAllOrderItemByOrderId(order.getOrderId());

            for (OrderItem orderItem : orderItems) {
                // TODO: Check expiration?
                Service service = serviceRepository.getServiceByServiceId(orderItem.getServiceId());
                List<ServiceType> typeList = serviceTypeRepository.findAllByServiceId(service.getServiceId()).orElse(null);

                PurchasedServiceDTO purchase = new PurchasedServiceDTO();
                purchase.setServiceId(service.getServiceId());
                purchase.setTitleName(service.getTitleName());
                purchase.setTypeList(typeList);
                purchase.setGroupType(service.getGroupType());

                purchases.add(new PurchasedServiceDTO(orderItem.getServiceId()));
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(purchases);
    }

    public ResponseEntity<String> createHourlySchedule(HttpServletRequest request, HourlyScheduleDTO scheduleDTO) {
        // Validate service ID
        ResponseEntity<String> serviceIdValidation = validateServiceId(scheduleDTO.getServiceId(), request);
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
        storeToDatabase(scheduleDTO, request);

        // TODO: Send notification to staff

        return ResponseEntity.status(HttpStatus.OK).body("Set schedule successfully! Please wait for our staff apply this job!");
    }

    public ResponseEntity<String> createReturnSchedule(HttpServletRequest request, ReturnScheduleDTO scheduleDTO) {
        final int MINIMUM_RETURN_HOURS = 4;

        // Validate service ID
        ResponseEntity<String> serviceIdValidation = validateServiceId(scheduleDTO.getServiceId(), request);
        if (serviceIdValidation != null) return serviceIdValidation;

        // Validate pickupDate > current + 3hr
        LocalDateTime pickupDate = scheduleDTO.getPickupDate().atTime(scheduleDTO.getTime());
        ResponseEntity<String> pickupDateValidation = validateDate(LocalDateTime.now(), pickupDate, FIND_STAFF_HOURS, "pickup date");
        if (pickupDateValidation != null) return pickupDateValidation;

        // Validate receivedDate > pickupDate + 4
        LocalDateTime receivedDate = scheduleDTO.getReceivedDate().atTime(scheduleDTO.getReceivedTime());
        ResponseEntity<String> receivedDateValidation = validateDate(pickupDate, receivedDate, MINIMUM_RETURN_HOURS, "received date");
        if (receivedDateValidation != null) return receivedDateValidation;

        // Store to database
        storeToDatabase(scheduleDTO, request);

        // TODO: Send notification to staff

        return ResponseEntity.status(HttpStatus.OK).body("Set schedule successfully! Please wait for our staff apply this job!");
    }

    public ResponseEntity<String> createDeliverySchedule(HttpServletRequest request, DeliveryScheduleDTO scheduleDTO) {
        // Validate service ID
        ResponseEntity<String> serviceIdValidation = validateServiceId(scheduleDTO.getServiceId(), request);
        if (serviceIdValidation != null) return serviceIdValidation;

        // Validate date > current + 3
        LocalDateTime date = scheduleDTO.getDate().atTime(scheduleDTO.getTime());
        ResponseEntity<String> receivedDateValidation = validateDate(LocalDateTime.now(), date, FIND_STAFF_HOURS, "date");
        if (receivedDateValidation != null) return receivedDateValidation;

        // (MAY OCCUR BUGS) Validate quantity in DTO is smaller than quantity in order
        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        System.out.println(scheduleDTO.getServiceId() + " === " + userId);
        int sumOfQuantityRetrieve = scheduleRepository.getSumOfQuantityRetrieve(scheduleDTO.getServiceId(), userId);
        int quantityFromOrder = 0;
        List<Order> orders = orderRepository.getAllOrderCompleteByUserId(userId);
        for (Order order : orders) {
            List<OrderItem> orderItems = orderItemRepository.getAllOrderItemByOrderId(order.getOrderId());

            for (OrderItem orderItem : orderItems) {
                if (orderItem.getServiceId() != scheduleDTO.getServiceId()) continue;
                quantityFromOrder += orderItem.getQuantity();
            }
        }

        if (sumOfQuantityRetrieve + scheduleDTO.getQuantity() > quantityFromOrder) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You are out of quantity. Please purchase with quantity lower than " + (quantityFromOrder - sumOfQuantityRetrieve));
        }

        // Store to database
        storeToDatabase(scheduleDTO, request);

        // TODO: Send notification to staff

        return ResponseEntity.status(HttpStatus.OK).body("Set schedule successfully! Please wait for our staff apply this job!");
    }

    private ResponseEntity<String> validateServiceId(int serviceId, HttpServletRequest request) {
        // Validate service ID
        Service service = serviceRepository.getServiceByServiceId(serviceId);
        if (service == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Can't find this service ID");
        }

        // Validate serviceId is in order
        if (!isContainsServiceId(getAllPurchased(request).getBody(), serviceId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("You haven't buy this service");
        }

        // TODO: Check serviceID is correct type (Waiting feature/services ...)

        return null;
    }

    private boolean isContainsServiceId(Set<PurchasedServiceDTO> purchases, int serviceId) {
        if (purchases == null) return false;

        for (PurchasedServiceDTO purchase : purchases) {
            if (purchase.getServiceId() == serviceId) {
                return true; // ServiceId found in the array
            }
        }
        return false; // ServiceId not found in the array
    }


    private ResponseEntity<String> validateDate(LocalDateTime startDate, LocalDateTime endDate, int hours, String varName) {
        // Check endTime in office hours
        int endHour = endDate.getHour();
        if (endHour <= 6 || endHour >= 19) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please set your " + varName + " in range from 7:00 to 20:00");
        }

        // Check is valid working date
        LocalDateTime startWorkingDate = startDate.plusHours(hours);
        if (endDate.isAfter(startWorkingDate)) return null;

        // Check workingDate in office hours
        if (startWorkingDate.getHour() <= 6 || startWorkingDate.getHour() >= 19) {
            String formattedDate = startWorkingDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You must set your " + varName + " after 7:00:00 " + formattedDate);
        }

        // Validate endDate > startDate + n hours
        String formattedDate = startWorkingDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You must set your " + varName + " after " + formattedDate);
    }

    private void storeToDatabase(Object scheduleDTO, HttpServletRequest request) {
        int customerId = authorizationUtil.getUserIdFromAuthorizationHeader(request);

        // Return Service
        if (scheduleDTO instanceof ReturnScheduleDTO) {
            List<Schedule> schedules = scheduleMapper.mapToEntity((ReturnScheduleDTO) scheduleDTO);

            for (Schedule schedule : schedules) {
                schedule.setCustomerId(customerId);
                scheduleRepository.save(schedule);
            }

            return;
        }

        // Hourly and Delivery Service
        Schedule schedule = null;
        if (scheduleDTO instanceof HourlyScheduleDTO) {
            schedule = scheduleMapper.mapToEntity((HourlyScheduleDTO) scheduleDTO);
        } else if (scheduleDTO instanceof DeliveryScheduleDTO) {
            schedule = scheduleMapper.mapToEntity((DeliveryScheduleDTO) scheduleDTO);
        }

        // Handle NullPointerException
        if (schedule == null) return;

        // Store to database
        schedule.setCustomerId(customerId);
        scheduleRepository.save(schedule);
    }

}
