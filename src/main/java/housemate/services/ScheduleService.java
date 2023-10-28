package housemate.services;

import housemate.constants.Cycle;
import housemate.entities.*;
import housemate.mappers.ScheduleMapper;
import housemate.models.ScheduleDTO;
import housemate.repositories.*;
import housemate.responses.EventRes;
import housemate.responses.PurchasedServiceRes;
import housemate.utils.AuthorizationUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author hdang09
 */
@org.springframework.stereotype.Service
public class ScheduleService {

    private static final int OFFICE_HOURS_START = 6;
    private static final int OFFICE_HOURS_END = 18;
    private static final int FIND_STAFF_HOURS = 3;
    private static final int MINIMUM_RETURN_HOURS = 4;
    private static final String RETURN_SERVICE = "RETURN_SERVICE"; // Hard code (This is special service => create 2 schedule)
    private final ServiceRepository serviceRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleMapper scheduleMapper;
    private final AuthorizationUtil authorizationUtil;
    private final ServiceTypeRepository serviceTypeRepository;
    private final UserUsageRepository userUsageRepository;
    private final UserRepository userRepository;

    @Autowired
    public ScheduleService(
            ServiceRepository serviceRepository,
            ScheduleRepository scheduleRepository,
            ScheduleMapper scheduleMapper,
            AuthorizationUtil authorizationUtil,
            ServiceTypeRepository serviceTypeRepository,
            UserUsageRepository userUsageRepository,
            UserRepository userRepository
    ) {
        this.serviceRepository = serviceRepository;
        this.scheduleRepository = scheduleRepository;
        this.scheduleMapper = scheduleMapper;
        this.authorizationUtil = authorizationUtil;
        this.serviceTypeRepository = serviceTypeRepository;
        this.userUsageRepository = userUsageRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<List<EventRes>> getScheduleForCustomer(HttpServletRequest request) {
        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        List<EventRes> events = new ArrayList<>();
        List<Schedule> schedules = scheduleRepository.getByCustomerId(userId);

        for (Schedule schedule : schedules) {
            Service service = serviceRepository.getServiceByServiceId(schedule.getServiceId());

            if (service.getGroupType().equals(RETURN_SERVICE)) {
                EventRes pickupEvent = scheduleMapper.mapToEventRes(schedule, service);
                pickupEvent.setEnd(pickupEvent.getStart().plusHours(1));
                setStaffInfo(events, schedule, pickupEvent);

                EventRes receivedEvent = scheduleMapper.mapToEventRes(schedule, service);
                receivedEvent.setStart(receivedEvent.getEnd());
                receivedEvent.setEnd(receivedEvent.getEnd().plusHours(1));
                setStaffInfo(events, schedule, receivedEvent);
            } else {
                EventRes event = scheduleMapper.mapToEventRes(schedule, service);
                setStaffInfo(events, schedule, event);
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(events);
    }

    public ResponseEntity<List<EventRes>> getScheduleForStaff(HttpServletRequest request) {
        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        List<EventRes> events = getEventsForStaff(userId);
        return ResponseEntity.status(HttpStatus.OK).body(events);
    }

    public ResponseEntity<List<EventRes>> getStaffScheduleByUserId(int userId) {
        List<EventRes> events = getEventsForStaff(userId);
        return ResponseEntity.status(HttpStatus.OK).body(events);
    }

    public ResponseEntity<Set<PurchasedServiceRes>> getAllPurchased(HttpServletRequest request) {
        Set<PurchasedServiceRes> purchases = new HashSet<>();
        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        List<UserUsage> usageList = userUsageRepository.getAllUserUsageByUserIdAndNotExpired(userId);

        // Get all serviceID based on order ID
        for (UserUsage userUsage : usageList) {
            // Check exist purchase in purchases Set (Set<PurchasedServiceRes>)
            PurchasedServiceRes existedPurchase = getPurchaseById(purchases, userUsage.getServiceId());
            if (existedPurchase != null) {
                existedPurchase.getUsages().add(userUsage);
                purchases.add(existedPurchase);
                continue;
            }

            // Check expiration and run out of remaining
            if (userUsage.getRemaining() == 0 && userUsage.getEndDate().isAfter(LocalDateTime.now())) continue;

            Service service = serviceRepository.getServiceByServiceId(userUsage.getServiceId());
            List<ServiceType> typeList = serviceTypeRepository.findAllByServiceId(service.getServiceId()).orElse(null);

            PurchasedServiceRes purchase = new PurchasedServiceRes();
            purchase.setServiceId(service.getServiceId());
            purchase.setTitleName(service.getTitleName());
            purchase.setType(typeList);
            purchase.setGroupType(service.getGroupType());
            purchase.getUsages().add(userUsage);

            purchases.add(purchase);
        }

        return ResponseEntity.status(HttpStatus.OK).body(purchases);
    }

    public PurchasedServiceRes getPurchaseById(Set<PurchasedServiceRes> serviceSet, int serviceId) {
        return serviceSet.stream()
                .filter(service -> service.getServiceId() == serviceId)
                .findFirst()
                .orElse(null);
    }

    public ResponseEntity<String> createSchedule(HttpServletRequest request, ScheduleDTO scheduleDTO) {
        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        Schedule schedule = scheduleMapper.mapToEntity(scheduleDTO);
        return validateAndProcessSchedule(userId, scheduleDTO.getServiceId(), scheduleDTO.getUserUsageId(), schedule);
    }

    // ======================================== REUSABLE FUNCTIONS ========================================

    private List<EventRes> getEventsForStaff(int staffId) {
        List<EventRes> events = new ArrayList<>();
        List<Schedule> schedules = scheduleRepository.getByStaffId(staffId);

        for (Schedule schedule : schedules) {
            Service service = serviceRepository.getServiceByServiceId(schedule.getServiceId());

            if (service.getGroupType().equals(RETURN_SERVICE)) {
                EventRes pickupEvent = scheduleMapper.mapToEventRes(schedule, service);
                pickupEvent.setEnd(pickupEvent.getStart().plusHours(1));
                setCustomerInfo(events, schedule, pickupEvent);

                EventRes receivedEvent = scheduleMapper.mapToEventRes(schedule, service);
                receivedEvent.setStart(receivedEvent.getEnd());
                receivedEvent.setEnd(receivedEvent.getEnd().plusHours(1));
                setCustomerInfo(events, schedule, receivedEvent);
            } else {
                EventRes event = scheduleMapper.mapToEventRes(schedule, service);
                setCustomerInfo(events, schedule, event);
            }
        }

        return events;
    }

    private void setStaffInfo(List<EventRes> events, Schedule schedule, EventRes event) {
        if (schedule.getStaffId() != 0) {
            UserAccount staff = userRepository.findByUserId(schedule.getStaffId());
            event.setStaff(staff.getFullName());
            event.setPhone(staff.getPhoneNumber());
        }
        events.add(event);
    }

    private void setCustomerInfo(List<EventRes> events, Schedule schedule, EventRes event) {
        UserAccount customer = userRepository.findByUserId(schedule.getCustomerId());
        event.setStaff(customer.getFullName());
        event.setPhone(customer.getPhoneNumber());

        events.add(event);
    }

    private ResponseEntity<String> validateAndProcessSchedule(int userId, int serviceId, int userUsageId, Schedule schedule) {
        // Check service not exist
        Service service = serviceRepository.getServiceByServiceId(serviceId);
        if (service == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Can not find that service ID");
        }

        // Check correct user usage ID
        UserUsage userUsage = userUsageRepository.findById(userUsageId).orElse(null);
        if (userUsage == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please input correct userUsageID");
        }
        if (userUsage.getServiceId() != serviceId) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User usage ID is not correct from service ID");
        }

        // Validate service ID
        ResponseEntity<String> serviceIdValidation = validateServiceId(serviceId, userId);
        if (serviceIdValidation != null) return serviceIdValidation;

        // Validate start date
        LocalDateTime startDate = schedule.getStartDate();
        ResponseEntity<String> startDateValidation = validateDate(LocalDateTime.now(), startDate, FIND_STAFF_HOURS, "start time");
        if (startDateValidation != null) return startDateValidation;

        // Validate end date
        LocalDateTime endDate = schedule.getEndDate();
        String groupType = serviceRepository.getServiceByServiceId(serviceId).getGroupType();
        ResponseEntity<String> endDateValidation = validateDate(startDate, endDate, groupType.equals(RETURN_SERVICE) ? MINIMUM_RETURN_HOURS : 1, "end time");
        if (endDateValidation != null) return endDateValidation;

        // Validate out range of cycle
        if (endDate.isAfter(userUsage.getEndDate())) {
            String formattedDate = userUsage.getEndDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You have set your date out of range. Please set before " + formattedDate);
        }

        // Map to entity
        schedule.setCustomerId(userId);
        schedule.setUserUsageId(userUsageId);

        // Validate quantity
        if (schedule.getQuantityRetrieve() > userUsage.getRemaining()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You are out of quantity. Please choose another User Usage");
        }

        // Store to database
        storeToDatabase(schedule);

        return ResponseEntity.status(HttpStatus.OK).body("Set schedule successfully! Please wait for our staff to apply this job!");
    }

    private ResponseEntity<String> validateServiceId(int serviceId, int userId) {
        // Validate service ID
        Service service = serviceRepository.getServiceByServiceId(serviceId);
        if (service == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Can't find this service ID");
        }

        // Get all service ID that user has purchased
        Set<Integer> purchasedServiceIds = new HashSet<>();
        List<UserUsage> usageList = userUsageRepository.getAllUserUsageByUserIdAndNotExpired(userId);
        for (UserUsage userUsage : usageList) {
            purchasedServiceIds.add(userUsage.getServiceId());
        }

        // Validate serviceId is in order
        if (isNotContainsServiceId(purchasedServiceIds, serviceId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("You haven't buy this service");
        }

        // TODO: Check serviceID is correct type (Waiting feature/services ...)

        return null;
    }

    private boolean isNotContainsServiceId(Set<Integer> serviceIds, int serviceId) {
        return serviceIds.stream().noneMatch(p -> p == serviceId);
    }

    private ResponseEntity<String> validateDate(LocalDateTime startDate, LocalDateTime endDate, int hours, String varName) {
        // Check endTime in office hours
        int endHour = endDate.getHour();
        if (endHour <= OFFICE_HOURS_START || endHour >= OFFICE_HOURS_END) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please set your " + varName + " in range from 7:00 to 18:00");
        }

        // Check is valid working date
        LocalDateTime startWorkingDate = startDate.plusHours(hours);
        if (endDate.isAfter(startWorkingDate)) return null;

        // Check workingDate in office hours
        if (startWorkingDate.getHour() <= OFFICE_HOURS_START || startWorkingDate.getHour() >= OFFICE_HOURS_END) {
            String formattedDate = startWorkingDate.plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You must set your " + varName + " after 7:00:00 " + formattedDate);
        }

        // Validate endDate > startDate + n hours
        String formattedDate = startWorkingDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You must set your " + varName + " after " + formattedDate);
    }

    private void storeToDatabase(Schedule schedule) {
        int customerId = schedule.getCustomerId();
        Cycle cycle = schedule.getCycle();
        int parentScheduleId = 0;

        // Usage of user
        UserUsage userUsage = userUsageRepository.findById(schedule.getUserUsageId()).orElse(null);
        if (userUsage == null) return;

        // Get max quantity
        int maxQuantity = getMaxQuantity(userUsage.getEndDate(), cycle, userUsage.getRemaining(), schedule.getQuantityRetrieve());

        // Store to database (ONLY_ONE_TIME)
        if (cycle == Cycle.ONLY_ONE_TIME) {
            schedule.setCustomerId(customerId);
            Schedule newSchedule = scheduleRepository.save(schedule);

            // Update schedule parent ID
            newSchedule.setParentScheduleId(newSchedule.getScheduleId());
            scheduleRepository.save(newSchedule);
            return;
        }

        // Store to database (EVERY_WEEK)
        if (cycle == Cycle.EVERY_WEEK) {
            for (int week = 0; week < maxQuantity; week++) {
                // Create new instance for schedule
                Schedule newSchedule = schedule.clone();

                newSchedule.setCustomerId(customerId);
                newSchedule.setStartDate(newSchedule.getStartDate().plusWeeks(week));
                newSchedule.setEndDate(newSchedule.getEndDate().plusWeeks(week));

                // Store parent schedule ID
                Schedule scheduleDb = scheduleRepository.save(newSchedule);
                parentScheduleId = week == 0 ? scheduleDb.getScheduleId() : parentScheduleId;
                scheduleDb.setParentScheduleId(parentScheduleId);
                scheduleRepository.save(scheduleDb);
            }
            return;
        }

        // Store to database (EVERY_MONTH)
        for (int month = 0; month < maxQuantity; month++) {
            // Create new instance for schedule
            Schedule newSchedule = schedule.clone();

            newSchedule.setCustomerId(customerId);
            newSchedule.setStartDate(newSchedule.getStartDate().plusMonths(month));
            newSchedule.setEndDate(newSchedule.getEndDate().plusMonths(month));

            // Store parent schedule ID
            Schedule scheduleDb = scheduleRepository.save(newSchedule);
            parentScheduleId = month == 0 ? scheduleDb.getParentScheduleId() : parentScheduleId;
            scheduleDb.setParentScheduleId(month == 0 ? scheduleDb.getParentScheduleId() : parentScheduleId);
            scheduleRepository.save(scheduleDb);
        }
    }

    private int getMaxQuantity(LocalDateTime date, Cycle cycle, int remaining, int quantity) {
        int maxForCycle = 1; // Default for ONLY_ONE_TIME
        LocalDateTime now = LocalDateTime.now();

        if (cycle == Cycle.EVERY_WEEK) {
            maxForCycle = (int) ChronoUnit.WEEKS.between(now, date);
        }

        if (cycle == Cycle.EVERY_MONTH) {
            maxForCycle = (int) ChronoUnit.MONTHS.between(now, date);
        }

        return Math.min(maxForCycle, quantity == 0 ? remaining : Math.floorDiv(remaining, quantity));
    }
}
