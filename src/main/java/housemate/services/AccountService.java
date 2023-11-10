package housemate.services;

import housemate.constants.Role;
import housemate.entities.*;
import housemate.mappers.AccountMapper;
import housemate.models.CreateAccountDTO;
import housemate.models.UpdateAccountDTO;
import housemate.repositories.*;
import housemate.responses.CustomerDetailRes;
import housemate.responses.CustomerRes;
import housemate.responses.ReportRes;
import housemate.responses.StaffRes;
import housemate.utils.AuthorizationUtil;
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

    public ResponseEntity<List<UserAccount>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(userRepository.findAll());
    }

    public ResponseEntity<UserAccount> getInfo(int userId) {
        UserAccount account = userRepository.findByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(account);
    }

    public ResponseEntity<String> updateInfo(UpdateAccountDTO updateAccountDTO, int userId) {
        // Get account in database
        UserAccount accountDB = userRepository.findByUserId(userId);

        // Update account
        UserAccount account = accountMapper.updateAccount(accountDB, updateAccountDTO);
        userRepository.save(account);

        return ResponseEntity.status(HttpStatus.OK).body("Updated successfully!");
    }

    public ResponseEntity<String> delete(int userId) {
        // Find account in database
        UserAccount account = userRepository.findByUserId(userId);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cannot find this account!");
        }

        // Delete account
        userRepository.deleteById(userId);
        return ResponseEntity.status(HttpStatus.OK).body("Deleted successfully!");
    }
    public ResponseEntity<String> changeRole(int userId, Role role) {
        userRepository.updateRole(userId, role);
        return ResponseEntity.status(HttpStatus.OK).body("Changed role successfully!");
    }

    public ResponseEntity<List<CustomerRes>> getAllCustomer() {
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

    public ResponseEntity<List<StaffRes>> getAllStaff() {
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

    public ResponseEntity<List<UserAccount>> getAllAdmin() {
        List<UserAccount> staffs = userRepository.findByRole(Role.ADMIN);
        return ResponseEntity.status(HttpStatus.OK).body(staffs);
    }

    public ResponseEntity<UserAccount> getCurrentUser(HttpServletRequest request) {
        int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        return getInfo(userId);
    }

    public ResponseEntity<String> createStaffAccount(CreateAccountDTO accountDTO) {
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

        UserAccount account = accountMapper.mapToEntity(accountDTO);
        userRepository.save(account);
        return ResponseEntity.status(HttpStatus.OK).body("Create staff successfully!");
    }

    public ResponseEntity<?> getCustomerDetail(int customerId, String start, String end) {
        CustomerDetailRes customerDetailRes = new CustomerDetailRes();

        final String MONTH_YEAR_FORMAT = "yyyy/MM";
        if (!isValidFormat(MONTH_YEAR_FORMAT, start) && !isValidFormat(MONTH_YEAR_FORMAT, end)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Must be in format " + MONTH_YEAR_FORMAT);
        }

        LocalDateTime startDate = YearMonth.parse(start, DateTimeFormatter.ofPattern(MONTH_YEAR_FORMAT)).atDay(1).atStartOfDay();
        LocalDateTime endDate = YearMonth.parse(end, DateTimeFormatter.ofPattern(MONTH_YEAR_FORMAT)).atEndOfMonth().atStartOfDay();

        List<ReportRes> reports = scheduleRepository.getMonthlyReport(customerId, startDate, endDate);
        for (ReportRes report: reports) {
            Service service = serviceRepository.getServiceByServiceId(report.getServiceId());
            report.setServiceName(service.getTitleName());
            report.setUnitOfMeasure(service.getUnitOfMeasure());
        }

        List<Schedule> schedules = scheduleRepository.getHistoryUsage(customerId);
        for (Schedule schedule: schedules) {
            UserUsage userUsage = userUsageRepository.findById(schedule.getUserUsageId()).orElse(null);
            OrderItem orderItem = orderItemRepository.findById(userUsage.getOrderItemId());
            Service service = serviceRepository.findById(orderItem.getServiceId()).orElse(null);
            schedule.setService(service);

        }

        customerDetailRes.setNumberOfOrder(orderRepository.countByUserId(customerId));
        customerDetailRes.setAmountSpent(orderRepository.sumFinalPriceByUserId(customerId));
        customerDetailRes.setMonthlyReport(reports);
        customerDetailRes.setUsageHistory(schedules);
        customerDetailRes.setUserInfo(userRepository.findByUserId(customerId));
        return ResponseEntity.status(HttpStatus.OK).body(customerDetailRes);
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
