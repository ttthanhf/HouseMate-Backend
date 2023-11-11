package housemate.services;

import housemate.constants.Cycle;
import housemate.constants.DeleteType;
import housemate.constants.ServiceConfiguration;
import housemate.constants.Role;
import housemate.constants.ScheduleStatus;
import housemate.entities.*;
import housemate.mappers.ScheduleMapper;
import housemate.models.ScheduleDTO;
import housemate.models.ScheduleUpdateDTO;
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

    private static final String RETURN_SERVICE = "RETURN_SERVICE"; // This is special service => create 2 schedule
    private final ServiceRepository serviceRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleMapper scheduleMapper;
    private final AuthorizationUtil authorizationUtil;
    private final ServiceTypeRepository serviceTypeRepository;
    private final UserUsageRepository userUsageRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final ServiceConfigRepository serviceConfigRepository;
    private final TaskService taskService;
    private final int OFFICE_HOURS_START;
    private final int OFFICE_HOURS_END;
    private final int FIND_STAFF_HOURS;
    private final int MINIMUM_RETURN_HOURS;


    @Autowired
    public ScheduleService(
            ServiceRepository serviceRepository,
            ScheduleRepository scheduleRepository,
            ScheduleMapper scheduleMapper,
            AuthorizationUtil authorizationUtil,
            ServiceTypeRepository serviceTypeRepository,
            UserUsageRepository userUsageRepository,
            UserRepository userRepository,
            OrderItemRepository orderItemRepository,
            ServiceConfigRepository serviceConfigRepository,
            TaskService taskService
    ) {
        this.serviceRepository = serviceRepository;
        this.scheduleRepository = scheduleRepository;
        this.scheduleMapper = scheduleMapper;
        this.authorizationUtil = authorizationUtil;
        this.serviceTypeRepository = serviceTypeRepository;
        this.userUsageRepository = userUsageRepository;
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;
        this.serviceConfigRepository = serviceConfigRepository;
        this.taskService = taskService;
        this.OFFICE_HOURS_START = Integer.parseInt(serviceConfigRepository.findFirstByConfigType(ServiceConfiguration.OFFICE_HOURS_START).getConfigValue());
        this.OFFICE_HOURS_END = Integer.parseInt(serviceConfigRepository.findFirstByConfigType(ServiceConfiguration.OFFICE_HOURS_END).getConfigValue());
        this.FIND_STAFF_HOURS = Integer.parseInt(serviceConfigRepository.findFirstByConfigType(ServiceConfiguration.FIND_STAFF_HOURS).getConfigValue());
        this.MINIMUM_RETURN_HOURS = Integer.parseInt(serviceConfigRepository.findFirstByConfigType(ServiceConfiguration.MINIMUM_RETURN_HOURS).getConfigValue());
    }

    private List<EventRes> getCustomerSchedule(int userId) {
        List<EventRes> events = new ArrayList<>();
        List<Schedule> schedules = scheduleRepository.getByCustomerId(userId);

        for (Schedule schedule : schedules) {
            // Check if schedule is before current date
            boolean isBeforeCurrentDate = schedule.getEndDate().isBefore(LocalDateTime.now());
            if (isBeforeCurrentDate) {
                // TODO: Notification to customer that staff haven't applied
                schedule.setStatus(ScheduleStatus.CANCEL);
                scheduleRepository.save(schedule);
            }

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

        return events;
    }

    public ResponseEntity<List<EventRes>> getScheduleForCurrentUser(HttpServletRequest request) {
        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        Role userRole = Role.valueOf(authorizationUtil.getRoleFromAuthorizationHeader(request));

        if (userRole.equals(Role.CUSTOMER)) {
            List<EventRes> events = getCustomerSchedule(userId);
            return ResponseEntity.status(HttpStatus.OK).body(events);
        } else {
            return getStaffScheduleByUserId(userId);
        }
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
        for (UserUsage uu : usageList) {
            // Create new instance for user usage (clone)
            UserUsage userUsage = uu.clone();

            // Check expiration and run out of remaining
            int totalUsed = scheduleRepository.getTotalQuantityRetrieveByUserUsageId(userUsage.getUserUsageId());
            int remaining = userUsage.getRemaining() - totalUsed;
            if (remaining <= 0 || userUsage.getEndDate().isBefore(LocalDateTime.now())) continue;

            // Query the relationship to get data in database
            int serviceId = userUsage.getServiceId();
            int orderItemId = userUsage.getOrderItemId();
            OrderItem orderItem = orderItemRepository.findById(orderItemId);
            Service service = serviceRepository.getServiceByServiceId(serviceId);
            Service serviceChild = serviceRepository.getServiceByServiceId(orderItem.getServiceId());
            userUsage.setService(serviceChild);
            userUsage.setRemaining(remaining);

            // Add new UserUsage to the existedPurchase in purchases Set (Set<PurchasedServiceRes>)
            PurchasedServiceRes existedPurchase = getPurchaseById(purchases, serviceId);
            if (existedPurchase != null) {
                existedPurchase.getUsages().add(userUsage);
                purchases.add(existedPurchase);
                continue;
            }

            // Type List
            List<ServiceType> typeList = serviceTypeRepository.findAllByServiceId(service.getServiceId()).orElse(null);

            // Create new PurchasedServiceRes
            PurchasedServiceRes purchase = new PurchasedServiceRes();
            purchase.setServiceId(service.getServiceId());
            purchase.setTitleName(service.getTitleName());
            purchase.setType(typeList);
            purchase.setGroupType(service.getGroupType());
            purchase.getUsages().add(userUsage);

            // Add to Set<PurchasedServiceRes>
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
        schedule.setCustomerId(userId);
        return validateAndProcessSchedule(schedule, null, request);
    }

    public ResponseEntity<String> updateSchedule(HttpServletRequest request, ScheduleUpdateDTO updateSchedule, int scheduleId) {
        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        Schedule currentSchedule = scheduleRepository.findById(scheduleId).orElse(null);

        // Check if schedule ID is not exist
        if (currentSchedule == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không thể tìm lịch này với ID: + " + scheduleId);
        }

        // Check if status is allowed or not
        ScheduleStatus status = currentSchedule.getStatus();
        if (status != ScheduleStatus.PROCESSING && status != ScheduleStatus.PENDING) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Không thể cập nhật lịch!");
        }

        // Check is valid cycle
        boolean isValidCycle = currentSchedule.getCycle().equals(updateSchedule.getCycle()) || updateSchedule.getCycle().equals(Cycle.ONLY_ONE_TIME);
        if (!isValidCycle) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Không thể cập nhật lịch với chu kỳ " + updateSchedule.getCycle());
        }

        Cycle oldCycle = currentSchedule.getCycle();
        Schedule newSchedule = scheduleMapper.updateSchedule(currentSchedule, updateSchedule);
        newSchedule.setCustomerId(userId);
        return validateAndProcessSchedule(newSchedule, oldCycle, request);
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
            event.setUserName(staff.getFullName());
            event.setPhone(staff.getPhoneNumber());
        }
        events.add(event);
    }

    private void setCustomerInfo(List<EventRes> events, Schedule schedule, EventRes event) {
        UserAccount customer = userRepository.findByUserId(schedule.getCustomerId());
        event.setUserName(customer.getFullName());
        event.setPhone(customer.getPhoneNumber());

        events.add(event);
    }

    private ResponseEntity<String> validateAndProcessSchedule(Schedule schedule, Cycle oldCycle, HttpServletRequest request) {
        int serviceId = schedule.getServiceId();

        // Check service not exist
        Service service = serviceRepository.getServiceByServiceId(serviceId);
        if (service == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không thể tìm dịch vụ với ID này");
        }

        // Check correct user usage ID
        UserUsage userUsage = userUsageRepository.findById(schedule.getUserUsageId()).orElse(null);
        if (userUsage == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vui lòng nhập đúng ID của lượng sử dụng");
        }
        if (userUsage.getServiceId() != serviceId) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID của lượng sử dụng không khớp với ID của dịch vụ");
        }

        // Validate service ID
        ResponseEntity<String> serviceIdValidation = validateServiceId(serviceId, schedule.getCustomerId());
        if (serviceIdValidation != null) return serviceIdValidation;

        // Validate date
        LocalDateTime startDate = schedule.getStartDate();
        LocalDateTime endDate = schedule.getEndDate();
        ResponseEntity<String> dateValidation = validateDate(startDate, endDate, service.getGroupType());
        if (dateValidation != null) return dateValidation;

        // Validate out range of cycle
        if (endDate.isAfter(userUsage.getEndDate())) {
            String formattedDate = userUsage.getEndDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bạn đã đặt lịch ngoài phạm vi của lượng sử dụng. Vui lòng đặt lịch trước " + formattedDate);
        }

        // Validate quantity
        int totalUsed = scheduleRepository.getTotalQuantityRetrieveByUserUsageId(schedule.getUserUsageId());
        int forecastQuantity = getMaxQuantity(startDate, userUsage.getEndDate(), schedule.getCycle(), userUsage.getRemaining(), schedule.getQuantityRetrieve(), totalUsed);
        if (forecastQuantity == 0 || forecastQuantity * schedule.getQuantityRetrieve() + totalUsed > userUsage.getRemaining()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bạn đã hết số lượng hoặc không đủ. Vui lòng chọn lượng sử dụng khác hoặc giảm số lượng!");
        }

        // Delete schedule
        boolean isCreate = oldCycle == null;
        if (!isCreate) {
            deleteSchedule(schedule, oldCycle);
        }

        // Store to database
        storeToDatabase(schedule, isCreate, request);

        return ResponseEntity.status(HttpStatus.OK).body("Đặt lịch thành công! Vui lòng đợi nhân viên chúng tôi nhận công việc này.");
    }

    void deleteSchedule(Schedule newSchedule, Cycle oldCycle) {
        boolean isUpdateOnlyOnce = newSchedule.getCycle().equals(Cycle.ONLY_ONE_TIME);

        // Delete schedule
        if (isUpdateOnlyOnce) {
            scheduleRepository.deleteById(newSchedule.getScheduleId());

            // Check if delete only once on parent
            boolean isUpdateOnParent = !oldCycle.equals(Cycle.ONLY_ONE_TIME);
            if (isUpdateOnParent) {
                scheduleRepository.updateChildrenSchedule(newSchedule.getScheduleId());
            }
        } else {
            scheduleRepository.deleteByScheduleIdGreaterThanEqualAndParentScheduleIdEquals(newSchedule.getScheduleId(), newSchedule.getParentScheduleId());
        }
    }

    private ResponseEntity<String> validateServiceId(int serviceId, int userId) {
        // Validate service ID
        Service service = serviceRepository.getServiceByServiceId(serviceId);
        if (service == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không thể tìm ID của dịch vụ này!");
        }

        // Get all service ID that user has purchased
        Set<Integer> purchasedServiceIds = new HashSet<>();
        List<UserUsage> usageList = userUsageRepository.getAllUserUsageByUserIdAndNotExpired(userId);
        for (UserUsage userUsage : usageList) {
            purchasedServiceIds.add(userUsage.getServiceId());
        }

        // Validate serviceId is in order
        if (isNotContainsServiceId(purchasedServiceIds, serviceId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bạn chưa mua dịch vụ này");
        }

        // TODO: Check serviceID is correct type (Waiting feature/services ...)

        return null;
    }

    private boolean isNotContainsServiceId(Set<Integer> serviceIds, int serviceId) {
        return serviceIds.stream().noneMatch(p -> p == serviceId);
    }

    private ResponseEntity<String> validateDate(LocalDateTime startDate, LocalDateTime endDate, String groupType) {
        // Check startDate < endDate
        if (!startDate.isBefore(endDate)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bạn phải đặt ngày bắt đầu trước ngày kết thúc");
        }

        // Validate startDate in office hours
        if (isOutsideOfficeHours(startDate)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bạn vui lòng đặt ngày bắt đầu trong giờ hành chính (7:00 - 18:00)");
        }

        // Validate endDate in office hours
        if (isOutsideOfficeHours(endDate)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bạn vui lòng đặt ngày kết thúc trong giờ hành chính (7:00 - 18:00)");
        }

        // Check if endDate is outside office hours => startDate in new day
        int differenceHours = groupType.equals(RETURN_SERVICE) ? MINIMUM_RETURN_HOURS : 1;
        LocalDateTime minimumEndDate = LocalDateTime.now().plusHours(FIND_STAFF_HOURS + differenceHours);
        if (isOutsideOfficeHours(minimumEndDate)) {
            // If minimumEndDate started on a next day
            boolean isNextDate = minimumEndDate.getHour() > OFFICE_HOURS_START;

            // Assign minimumEndDate at 7:00:00
            LocalDateTime newDate = minimumEndDate.withHour(7).withMinute(0).withSecond(0).withNano(0).plusDays(isNextDate ? 1 : 0);

            if (startDate.isBefore(newDate)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bạn vui lòng đặt ngày bắt đầu sau " + formatDateTime((newDate)));
            }
        }

        // Validate startDate >= now + FIND_STAFF_HOURS
        LocalDateTime startWorkingDate = LocalDateTime.now().plusHours(FIND_STAFF_HOURS);
        if (startDate.isBefore(startWorkingDate)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bạn vui lòng đặt ngày bắt đầu sau " + formatDateTime(startWorkingDate));
        }

        // Validate startDate >= now + differenceHours
        LocalDateTime endWorkingDate = startDate.plusHours(differenceHours);
        if (endDate.isBefore(endWorkingDate)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bạn vui lòng đặt ngày kết thúc sau " + formatDateTime(endWorkingDate));
        }

        return null;
    }

    private void storeToDatabase(Schedule schedule, boolean isCreate, HttpServletRequest request) {
        int customerId = schedule.getCustomerId();
        Cycle cycle = schedule.getCycle();
        int parentScheduleId = 0;

        // Usage of user
        UserUsage userUsage = userUsageRepository.findById(schedule.getUserUsageId()).orElse(null);
        if (userUsage == null) return;

        // Get max quantity
        int totalUsed = scheduleRepository.getTotalQuantityRetrieveByUserUsageId(schedule.getUserUsageId());
        int maxQuantity = getMaxQuantity(schedule.getStartDate(), userUsage.getEndDate(), cycle, userUsage.getRemaining(), schedule.getQuantityRetrieve(), totalUsed);

        // Store to database (ONLY_ONE_TIME)
        if (cycle == Cycle.ONLY_ONE_TIME) {
            schedule.setCustomerId(customerId);
            Schedule newSchedule = scheduleRepository.save(schedule);

            // Update schedule parent ID
            newSchedule.setParentScheduleId(newSchedule.getScheduleId());
            Schedule scheduleDb = scheduleRepository.save(newSchedule);

            if (isCreate) {
                taskService.createNewTask(request, scheduleDb.getScheduleId());
            }
            return;
        }

        // Store to database (EVERY_WEEK/EVERY_MONTH)
        for (int increment = 0; increment < maxQuantity; increment++) {
            // Create new instance for schedule
            Schedule newSchedule = schedule.clone();
            newSchedule.setCustomerId(customerId);

            if (cycle == Cycle.EVERY_WEEK) {
                newSchedule.setStartDate(newSchedule.getStartDate().plusWeeks(increment));
                newSchedule.setEndDate(newSchedule.getEndDate().plusWeeks(increment));
            } else if (cycle == Cycle.EVERY_MONTH) {
                newSchedule.setStartDate(newSchedule.getStartDate().plusMonths(increment));
                newSchedule.setEndDate(newSchedule.getEndDate().plusMonths(increment));
            }

            // Store parent schedule ID
            newSchedule = increment == 0 ? scheduleRepository.save(newSchedule) : newSchedule; // Get parentScheduleId = scheduleId
            parentScheduleId = increment == 0 ? newSchedule.getScheduleId() : parentScheduleId;
            newSchedule.setParentScheduleId(parentScheduleId);
            scheduleRepository.save(newSchedule);
        }

        if (isCreate) {
            taskService.createNewTask(request, parentScheduleId);
        }
    }

    private int getMaxQuantity(LocalDateTime startDate, LocalDateTime endDate, Cycle cycle, int remaining, int quantity, int totalUsed) {
        int maxForCycle = 1; // Default for ONLY_ONE_TIME

        if (cycle == Cycle.EVERY_WEEK) {
            maxForCycle = (int) ChronoUnit.WEEKS.between(startDate, endDate) + 1;
        }

        if (cycle == Cycle.EVERY_MONTH) {
            maxForCycle = (int) ChronoUnit.MONTHS.between(startDate, endDate) + 1;
        }

        int maximum = remaining - totalUsed;
        return Math.min(maxForCycle, quantity == 0 ? remaining : Math.floorDiv(maximum, quantity));
    }

    private boolean isOutsideOfficeHours(LocalDateTime date) {
        int hour = date.getHour();
        return hour <= OFFICE_HOURS_START || hour >= OFFICE_HOURS_END;
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public ResponseEntity<?> getScheduleById(HttpServletRequest request, int scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElse(null);
        if (schedule == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không thể tìm thấy lịch này!");
        }

        Service service = serviceRepository.findById(schedule.getServiceId()).orElse(null);
        if (service == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không thể tìm thấy dịch vụ này!");
        }

        List<ServiceType> typeList = serviceTypeRepository.findAllByServiceId(schedule.getServiceId()).orElse(null);
        schedule.setType(typeList);
        schedule.setServiceName(service.getTitleName());
        UserUsage currentUsage = userUsageRepository.findById(schedule.getUserUsageId()).orElse(null);
        if (currentUsage == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không thể tìm thấy lượng sử dụng này");
        }
        Service usageService = serviceRepository.findById(currentUsage.getServiceId()).orElse(null);
        if (usageService == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không thể tìm thấy dịch vụ trong lượng sử dụng này");
        }
        currentUsage.setService(usageService);
        schedule.setCurrentUsage(currentUsage);
        schedule.setGroupType(service.getGroupType());

        Set<PurchasedServiceRes> purchases = new HashSet<>();
        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        List<UserUsage> usageList = userUsageRepository.getAllByServiceIdAndUserId(schedule.getServiceId(), userId);

        // Get all serviceID based on order ID
        for (UserUsage uu : usageList) {
            // Create new instance for user usage (clone)
            UserUsage userUsage = uu.clone();

            // Check expiration and run out of remaining
            int totalUsed = scheduleRepository.getTotalQuantityRetrieveByUserUsageId(userUsage.getUserUsageId());
            int remaining = userUsage.getRemaining() - totalUsed;
            if (remaining <= 0 || userUsage.getEndDate().isBefore(LocalDateTime.now())) continue;

            // Query the relationship to get data in database
            int serviceId = userUsage.getServiceId();
            int orderItemId = userUsage.getOrderItemId();
            OrderItem orderItem = orderItemRepository.findById(orderItemId);
            Service serviceChild = serviceRepository.getServiceByServiceId(orderItem.getServiceId());
            userUsage.setService(serviceChild);
            userUsage.setRemaining(remaining);

            // Store to schedule
            schedule.getUsages().add(userUsage);
        }

        return ResponseEntity.status(HttpStatus.OK).body(schedule);
    }

    public ResponseEntity<String> cancelSchedule(HttpServletRequest request, int scheduleId, DeleteType deleteType) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElse(null);

        if (schedule == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không thể tìm thấy lịch này");
        }

        // Check if schedule is on task
        if (schedule.isOnTask() || schedule.getStaffId() != 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lịch này đã được lên danh sách nhận việc hoặc đã có nhân việc nhận công việc này!");
        }

        // Check if status is invalid or not
        ScheduleStatus status = schedule.getStatus();
        if (status != ScheduleStatus.PROCESSING && status != ScheduleStatus.PENDING) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bạn không thể hủy lịch này");
        }

        // Cancel schedule
        if (deleteType.equals(DeleteType.THIS_SCHEDULE)) {
            scheduleRepository.cancelThisSchedule(scheduleId);
        } else if (deleteType.equals(DeleteType.THIS_AND_FOLLOWING_SCHEDULE)) {
            scheduleRepository.cancelThisAndFollowingSchedule(scheduleId, schedule.getParentScheduleId());
        }

        // Cancel schedule => Cancel task
        taskService.cancelTask(request, scheduleId);

        return ResponseEntity.status(HttpStatus.OK).body("Hủy lịch thành công");
    }
}
