package housemate.services;

import housemate.constants.AccountStatus;
import housemate.constants.ImageType;
import housemate.constants.Role;
import housemate.entities.*;
import housemate.mappers.AccountMapper;
import housemate.models.CreateAccountDTO;
import housemate.models.UpdateAccountDTO;
import housemate.models.responses.MyPurchasedResponse;
import housemate.repositories.*;
import housemate.responses.*;
import housemate.utils.AuthorizationUtil;
import housemate.utils.BcryptUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@org.springframework.stereotype.Service
public class AccountService {

    private static final String MONTH_YEAR_FORMAT = "yyyy/MM";
    private static final String DEFAULT_STAFF_PASSWORD = "Password123";

    @Autowired
    UserRepository userRepository;

    @Autowired
    AccountMapper accountMapper;

    @Autowired
    AuthorizationUtil authorizationUtil;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ScheduleRepository scheduleRepository;

    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    UserUsageRepository userUsageRepository;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    PackageServiceItemRepository packageServiceItemRepository;

    @Autowired
    BcryptUtil bcryptUtil;

    public ResponseEntity<UserAccount> getInfo(int userId) {
        UserAccount account = userRepository.findByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(account);
    }

    public ResponseEntity<String> updateInfo(HttpServletRequest request, UpdateAccountDTO updateAccountDTO, int userId) {
        Role role = Role.valueOf(authorizationUtil.getRoleFromAuthorizationHeader(request));
        if (!role.equals(Role.ADMIN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You have no permission to access this function");
        }

        // Get account in database
        UserAccount accountDB = userRepository.findByUserId(userId);

        // Update account
        UserAccount account = accountMapper.updateAccount(accountDB, updateAccountDTO);
        userRepository.save(account);

        return ResponseEntity.status(HttpStatus.OK).body("Updated successfully!");
    }

    public ResponseEntity<String> delete(HttpServletRequest request, int userId) {
        Role role = Role.valueOf(authorizationUtil.getRoleFromAuthorizationHeader(request));
        if (!role.equals(Role.ADMIN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You have no permission to access this function");
        }

        // Find account in database
        UserAccount account = userRepository.findByUserId(userId);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cannot find this account!");
        }

        // Change isBanned to true
        account.setBanned(true);
        userRepository.save(account);
        return ResponseEntity.status(HttpStatus.OK).body("Deleted successfully!");
    }
    public ResponseEntity<String> changeRole(HttpServletRequest request, int userId, Role updateRole) {
        Role role = Role.valueOf(authorizationUtil.getRoleFromAuthorizationHeader(request));
        if (!role.equals(Role.ADMIN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You have no permission to access this function");
        }

        userRepository.updateRole(userId, updateRole);
        return ResponseEntity.status(HttpStatus.OK).body("Changed role successfully!");
    }

    public ResponseEntity<?> getAllCustomer(HttpServletRequest request) {
        Role role = Role.valueOf(authorizationUtil.getRoleFromAuthorizationHeader(request));
        if (!role.equals(Role.ADMIN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You have no permission to access this function");
        }

        List<CustomerRes> customers = new ArrayList<>();

        List<UserAccount> accounts = userRepository.findByRole(Role.CUSTOMER);
        for (UserAccount account: accounts) {
            int userId = account.getUserId();

            CustomerRes customer = new CustomerRes();
            customer.setId(userId);
            customer.setCustomerAvatar(account.getAvatar());
            customer.setCustomerName(account.getFullName());
            customer.setNumberOfSchedule(scheduleRepository.countByCustomerId(userId));
            customer.setAmountSpent(orderRepository.sumFinalPriceByUserId(userId));
            customer.setNumberOfTransactions(orderRepository.countByUserId(userId));
            customer.setJoinDate(account.getCreatedAt());

            customers.add(customer);
        }

        return ResponseEntity.status(HttpStatus.OK).body(customers);
    }

    public ResponseEntity<?> getAllStaff(HttpServletRequest request) {
        Role role = Role.valueOf(authorizationUtil.getRoleFromAuthorizationHeader(request));
        if (!role.equals(Role.ADMIN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You have no permission to access this function");
        }

        List<StaffRes> staffs = new ArrayList<>();

        List<UserAccount> accounts = userRepository.findByRole(Role.STAFF);
        for (UserAccount account: accounts) {
            int userId = account.getUserId();

            StaffRes staff = new StaffRes();
            staff.setId(userId);
            staff.setStaffAvatar(account.getAvatar());
            staff.setStaffName(account.getFullName());
            staff.setPoint(account.getProficiencyScore());
            staff.setStatus(account.getAccountStatus());
            // TODO: Waiting task
            staff.setNumberOfJobs(0);
            staff.setSuccessRate(0);

            staffs.add(staff);
        }

        return ResponseEntity.status(HttpStatus.OK).body(staffs);
    }

    public ResponseEntity<UserAccount> getCurrentUser(HttpServletRequest request) {
        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        return getInfo(userId);
    }

    public ResponseEntity<String> createStaffAccount(HttpServletRequest request, CreateAccountDTO accountDTO) {
        Role role = Role.valueOf(authorizationUtil.getRoleFromAuthorizationHeader(request));
        if (!role.equals(Role.ADMIN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You have no permission to access this function");
        }

        // Check age > 18
        if (LocalDate.now().minusYears(18).isBefore(accountDTO.getDateOfBirth())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Age must be larger than 18!");
        }

        // Check unique of email
        if (userRepository.findByEmailAddress(accountDTO.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This email address is existed before!");
        }

        // Check unique of identity card
        if (userRepository.findByIdentityCard(accountDTO.getIdentityCard()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This identity card is existed before!");
        }

        // Store to database
        UserAccount account = accountMapper.mapToEntity(accountDTO);
        String hash = bcryptUtil.hashPassword(DEFAULT_STAFF_PASSWORD);
        account.setPasswordHash(hash);
        userRepository.save(account);
        return ResponseEntity.status(HttpStatus.OK).body("Tạo tài khoản thành công! Mật khẩu cho tài khoản này là " + DEFAULT_STAFF_PASSWORD);
    }

    public ResponseEntity<?> getCustomerDetail(int customerId, String start, String end) {
        CustomerDetailRes customerDetailRes = new CustomerDetailRes();

        // Default sort
        if (start == null) {
            start = "1990/01";
        }
        if (end == null) {
            end = "3000/12";
        }

        // Check date format
        final String MONTH_YEAR_FORMAT = "yyyy/MM";
        if (!isValidFormat(MONTH_YEAR_FORMAT, start) && !isValidFormat(MONTH_YEAR_FORMAT, end)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Must be in format " + MONTH_YEAR_FORMAT);
        }

        // Convert string to LocalDateTime
        LocalDateTime startDate = YearMonth.parse(start, DateTimeFormatter.ofPattern(MONTH_YEAR_FORMAT)).atDay(1).atStartOfDay();
        LocalDateTime endDate = YearMonth.parse(end, DateTimeFormatter.ofPattern(MONTH_YEAR_FORMAT)).atEndOfMonth().atStartOfDay();

        // Get monthly report
        List<ReportRes> reports = scheduleRepository.getMonthlyReportForCustomer(customerId, startDate, endDate);
        for (ReportRes report: reports) {
            Service service = serviceRepository.getServiceByServiceId(report.getServiceId());
            report.setServiceName(service.getTitleName());
            report.setUnitOfMeasure(service.getUnitOfMeasure());
        }

        // Get usage history
        List<Schedule> schedules = scheduleRepository.getHistoryUsage(customerId);
        for (Schedule schedule: schedules) {
            schedule.setServiceName(schedule.getService().getTitleName());
            UserUsage userUsage = userUsageRepository.findById(schedule.getUserUsageId()).orElse(null);
            OrderItem orderItem = orderItemRepository.findById(userUsage.getOrderItemId());

            // Get images service
            Service service = serviceRepository.findById(orderItem.getServiceId()).orElse(null);
            if (service.isPackage()) {
                List<PackageServiceItem> listPackageServiceItem = packageServiceItemRepository.findAllSingleServiceIdByPackageServiceId(service.getServiceId());
                for (PackageServiceItem packageServiceItem : listPackageServiceItem) {
                    Service singleService = serviceRepository.getServiceByServiceId(packageServiceItem.getSingleServiceId());
                    List<Image> images = imageRepository.findAllByEntityIdAndImageType(singleService.getServiceId(), housemate.constants.ImageType.SERVICE).orElse(List.of());
                    singleService.setImages(images);
                }
            } else {
                List<Image> images = imageRepository.findAllByEntityIdAndImageType(service.getServiceId(), ImageType.SERVICE).orElse(List.of());
                service.setImages(images);
            }

            // Add to shedules
            schedule.setService(service);
        }

        // Get purchase history
        List<MyPurchasedResponse> purchaseHistory = new ArrayList<>();

        List<Order> listOrder = orderRepository.getAllOrderCompleteByUserId(customerId);
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
                        List<Image> images = imageRepository.findAllByEntityIdAndImageType(singleService.getServiceId(), housemate.constants.ImageType.SERVICE).orElse(List.of());
                        singleService.setImages(images);
                        listSingleServiceName.add(singleService.getTitleName());
                    }
                } else {
                    List<Image> images = imageRepository.findAllByEntityIdAndImageType(service.getServiceId(), ImageType.SERVICE).orElse(List.of());
                    service.setImages(images);
                    listSingleServiceName.add(service.getTitleName());
                }

                myPurchasedResponse.setOrderItemId(orderItem.getOrderItemId());
                myPurchasedResponse.setEndDate(orderItem.getExpireDate());
                myPurchasedResponse.setStartDate(orderItem.getCreateDate());
                myPurchasedResponse.setSingleServiceName(listSingleServiceName);
                myPurchasedResponse.setService(service);

                purchaseHistory.add(myPurchasedResponse);
            }
        }

        // Set to CustomerDetailRes
        customerDetailRes.setNumberOfOrder(orderRepository.countByUserId(customerId));
        customerDetailRes.setAmountSpent(orderRepository.sumFinalPriceByUserId(customerId));
        customerDetailRes.setMonthlyReport(reports);
        customerDetailRes.setUsageHistory(schedules);
        customerDetailRes.setUserInfo(userRepository.findByUserId(customerId));
        customerDetailRes.setPurchaseHistory(purchaseHistory);

        return ResponseEntity.status(HttpStatus.OK).body(customerDetailRes);
    }

    public ResponseEntity<?> getStaffDetail(HttpServletRequest request, int staffId, String start, String end) {
        Role role = Role.valueOf(authorizationUtil.getRoleFromAuthorizationHeader(request));
        if (!role.equals(Role.ADMIN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You have no permission to access this function");
        }

        StaffDetailRes staffDetailRes = new StaffDetailRes();

        // Default sort
        if (start == null) {
            start = "1990/01";
        }
        if (end == null) {
            end = "3000/12";
        }

        // Check date format
        if (!isValidFormat(MONTH_YEAR_FORMAT, start) && !isValidFormat(MONTH_YEAR_FORMAT, end)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Must be in format " + MONTH_YEAR_FORMAT);
        }

        // Convert string to LocalDateTime
        LocalDateTime startDate = YearMonth.parse(start, DateTimeFormatter.ofPattern(MONTH_YEAR_FORMAT)).atDay(1).atStartOfDay();
        LocalDateTime endDate = YearMonth.parse(end, DateTimeFormatter.ofPattern(MONTH_YEAR_FORMAT)).atEndOfMonth().atStartOfDay();

        // Get monthly report
        List<ReportRes> reports = scheduleRepository.getMonthlyReportForStaff(staffId, startDate, endDate);
        for (ReportRes report: reports) {
            Service service = serviceRepository.getServiceByServiceId(report.getServiceId());
            report.setServiceName(service.getTitleName());
            report.setUnitOfMeasure(service.getUnitOfMeasure());
        }

        // Get monthly report
        List<ReportRes> achievements = scheduleRepository.getStaffAchievement(staffId);
        for (ReportRes achievement: achievements) {
            Service service = serviceRepository.getServiceByServiceId(achievement.getServiceId());
            achievement.setServiceName(service.getTitleName());
            achievement.setUnitOfMeasure(service.getUnitOfMeasure());
        }

        // Set to StaffDetailRes
        staffDetailRes.setMonthlyReport(reports);
        staffDetailRes.setAchievement(achievements);
        staffDetailRes.setUserInfo(userRepository.findByUserId(staffId));

        return ResponseEntity.status(HttpStatus.OK).body(staffDetailRes);
    }

    private static boolean isValidFormat(String format, String value) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            Date date = dateFormat.parse(value);
            return value.equals(dateFormat.format(date));
        } catch (ParseException e) {
            return false;
        }
    }
}
