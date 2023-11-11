package housemate.services;

import com.google.analytics.data.v1beta.*;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import housemate.constants.Role;
import housemate.entities.Order;
import housemate.models.responses.AnalyticOverviewResponse;
import housemate.models.responses.AnalyticRevenueResponse;
import housemate.models.responses.AnalyticRevenueResponse.AllOrderPrice;
import housemate.models.responses.AnalyticUserResponse;
import housemate.models.responses.AnalyticUserResponse.AnalyticUserDetail;
import housemate.repositories.OrderRepository;
import housemate.repositories.ServiceRepository;
import housemate.repositories.UserRepository;
import housemate.utils.AuthorizationUtil;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import housemate.entities.Service;
import housemate.entities.UserAccount;
import housemate.models.AnalyticPageDTO;
import housemate.models.responses.AnalyticCustomerResponse;
import housemate.models.responses.AnalyticServicePageResponse;
import housemate.repositories.OrderItemRepository;
import housemate.repositories.ScheduleRepository;
import java.util.HashMap;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import housemate.constants.SortEnum;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;

@org.springframework.stereotype.Service
public class AnalyticService implements DisposableBean {

    @Value("${google.analytic.property-id}")
    private String propertyId;

    private final String credentialsFileName = "credentials.json";

    private GoogleCredentials credentials;
    private BetaAnalyticsDataSettings betaAnalyticsDataSettings;
    private BetaAnalyticsDataClient analyticsData;

    @Autowired
    private AuthorizationUtil authorizationUtil;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @PostConstruct
    public void init() {
        try {
            InputStream credStream = getClass().getClassLoader().getResourceAsStream("/" + credentialsFileName); //cho deloy server lấy kèm data
            if (credStream == null) { //do chạy trong môi trường dev nên sẽ ko lấy được dạng build nên check null để kiểm tra xem dang ở dev hay deloy
                credStream = new FileInputStream(getClass().getClassLoader().getResource(credentialsFileName).getFile()); //lấy cho dạng dev đọc được file
            }
            credentials = GoogleCredentials.fromStream(credStream);
            betaAnalyticsDataSettings = BetaAnalyticsDataSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                    .build();

            analyticsData = BetaAnalyticsDataClient.create(betaAnalyticsDataSettings);
        } catch (IOException ex) {
            Logger.getLogger(AnalyticService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void destroy() {
        if (analyticsData != null) {
            analyticsData.shutdown();
            try {
                if (!analyticsData.awaitTermination(5, TimeUnit.SECONDS)) {
                    analyticsData.shutdownNow();
                }
            } catch (InterruptedException e) {
                analyticsData.shutdownNow();
            }
        }
    }

    public ResponseEntity<?> getAnalyticUser(HttpServletRequest request, int dayAgo) {

        String role = authorizationUtil.getRoleFromAuthorizationHeader(request);
        if (!role.equals(Role.ADMIN.name())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient authority");
        }

        RunReportRequest reportRequest = RunReportRequest.newBuilder()
                .setProperty("properties/" + propertyId)
                .addDimensions(Dimension.newBuilder().setName("day"))
                .addDimensions(Dimension.newBuilder().setName("month"))
                .addDimensions(Dimension.newBuilder().setName("year"))
                .addMetrics(Metric.newBuilder().setName("active1DayUsers"))
                .addMetrics(Metric.newBuilder().setName("newUsers"))
                .addDateRanges(DateRange.newBuilder().setStartDate(dayAgo * 2 + 1 + "daysAgo").setEndDate("today"))
                .addOrderBys(
                        OrderBy.newBuilder()
                                .setDimension(OrderBy.DimensionOrderBy.newBuilder().setDimensionName("year"))
                                .setDesc(false))
                .addOrderBys(
                        OrderBy.newBuilder()
                                .setDimension(OrderBy.DimensionOrderBy.newBuilder().setDimensionName("month"))
                                .setDesc(false))
                .addOrderBys(
                        OrderBy.newBuilder()
                                .setDimension(OrderBy.DimensionOrderBy.newBuilder().setDimensionName("day"))
                                .setDesc(false))
                .build();

        RunReportResponse response = analyticsData.runReport(reportRequest);

        AnalyticUserResponse analyticUserResponse = new AnalyticUserResponse();
        analyticUserResponse.setBefore(new ArrayList<>());
        analyticUserResponse.setCurrent(new ArrayList<>());

        int sizeResponse = response.getRowsList().size();
        for (int before = 0, current = sizeResponse / 2; before < sizeResponse / 2; current++, before++) {

            Row rowCurrent = response.getRowsList().get(current);

            int yearCurrent = Integer.parseInt(rowCurrent.getDimensionValues(2).getValue());
            int monthCurrent = Integer.parseInt(rowCurrent.getDimensionValues(1).getValue());
            int dayCurrent = Integer.parseInt(rowCurrent.getDimensionValues(0).getValue());
            LocalDate dateCurrent = LocalDate.of(yearCurrent, monthCurrent, dayCurrent);

            int totalNewUserCurrent = Integer.parseInt(rowCurrent.getMetricValues(1).getValue());
            int totalActiveUserCurrent = Integer.parseInt(rowCurrent.getMetricValues(0).getValue());

            Row rowBefore = response.getRowsList().get(before);

            int yearBefore = Integer.parseInt(rowBefore.getDimensionValues(2).getValue());
            int monthBefore = Integer.parseInt(rowBefore.getDimensionValues(1).getValue());
            int dayBefore = Integer.parseInt(rowBefore.getDimensionValues(0).getValue());
            LocalDate dateBefore = LocalDate.of(yearBefore, monthBefore, dayBefore);

            int totalNewUserBefore = Integer.parseInt(rowBefore.getMetricValues(1).getValue());
            int totalActiveUserBefore = Integer.parseInt(rowBefore.getMetricValues(0).getValue());

            double percentTotalNewUserCurrent;
            if (totalNewUserBefore != 0) {
                percentTotalNewUserCurrent = ((double) (totalNewUserCurrent - totalNewUserBefore) / totalNewUserBefore);
            } else {
                percentTotalNewUserCurrent = totalNewUserCurrent;
            }
            percentTotalNewUserCurrent *= 100;

            double percentTotalActiveUserCurrent;
            if (totalNewUserBefore != 0) {
                percentTotalActiveUserCurrent = ((double) (totalActiveUserCurrent - totalActiveUserBefore) / totalActiveUserBefore);
            } else {
                percentTotalActiveUserCurrent = totalActiveUserCurrent;
            }
            percentTotalActiveUserCurrent *= 100;

            double percentTotalActiveUserBefore;
            if (totalActiveUserCurrent != 0) {
                percentTotalActiveUserBefore = ((double) (totalActiveUserBefore - totalActiveUserCurrent) / totalActiveUserCurrent);
            } else {
                percentTotalActiveUserBefore = totalActiveUserBefore;
            }
            percentTotalActiveUserBefore *= 100;

            double percentTotalNewUserBefore;
            if (totalNewUserCurrent != 0) {
                percentTotalNewUserBefore = ((double) (totalNewUserBefore - totalNewUserCurrent) / totalNewUserCurrent);
            } else {
                percentTotalNewUserBefore = totalNewUserBefore;
            }
            percentTotalNewUserBefore *= 100;

            AnalyticUserDetail analyticUserDetailCurrent = analyticUserResponse.new AnalyticUserDetail();
            analyticUserDetailCurrent.setDate(dateCurrent);
            analyticUserDetailCurrent.setTotalNewUser(totalNewUserCurrent);
            analyticUserDetailCurrent.setTotalActiveUser(totalActiveUserCurrent);
            analyticUserDetailCurrent.setPercentNewUser(percentTotalNewUserCurrent);
            analyticUserDetailCurrent.setPercentActiveUser(percentTotalActiveUserCurrent);
            analyticUserResponse.getCurrent().add(analyticUserDetailCurrent);

            AnalyticUserDetail analyticUserDetailBefore = analyticUserResponse.new AnalyticUserDetail();
            analyticUserDetailBefore.setDate(dateBefore);
            analyticUserDetailBefore.setTotalNewUser(totalNewUserBefore);
            analyticUserDetailBefore.setTotalActiveUser(totalActiveUserCurrent);
            analyticUserDetailBefore.setPercentActiveUser(percentTotalActiveUserBefore);
            analyticUserDetailBefore.setPercentNewUser(percentTotalNewUserBefore);
            analyticUserResponse.getBefore().add(analyticUserDetailBefore);

        }
        return ResponseEntity.status(HttpStatus.OK).body(analyticUserResponse);
    }

    public ResponseEntity<?> getAnalyticServicePage(HttpServletRequest request, AnalyticPageDTO analyticPageDTO) {

        String role = authorizationUtil.getRoleFromAuthorizationHeader(request);
        if (!role.equals(Role.ADMIN.name())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient authority");
        }

        LocalDate startDate = analyticPageDTO.getStartDate();
        LocalDate endDate = analyticPageDTO.getEndDate();
        int size = analyticPageDTO.getSize();
        int page = analyticPageDTO.getPage();

        RunReportRequest reportRequest = RunReportRequest.newBuilder()
                .setProperty("properties/" + propertyId)
                .addDimensions(Dimension.newBuilder().setName("pageTitle"))
                .addMetrics(Metric.newBuilder().setName("sessions"))
                .addDateRanges(DateRange.newBuilder().setStartDate(startDate.toString()).setEndDate(endDate.toString()))
                .build();

        RunReportResponse response = analyticsData.runReport(reportRequest);

        List<AnalyticServicePageResponse> listAnalyticServicePageResponse = new ArrayList<>();
        for (Row row : response.getRowsList()) {
            if (row.getDimensionValues(0).getValue().startsWith("Dịch Vụ | ")) {
                AnalyticServicePageResponse analyticServicePageResponse = new AnalyticServicePageResponse();

                String serviceName = row.getDimensionValues(0).getValue().split("\\|")[1].trim();
                analyticServicePageResponse.setServiceName(serviceName);

                int totalSessionView = Integer.parseInt(row.getMetricValues(0).getValue());
                analyticServicePageResponse.setTotalSessionView(totalSessionView);

                Service service = serviceRepository.getServiceByFullNameService(serviceName);
                int serviceId = service.getServiceId();

                double totalPriceOfService = orderItemRepository.sumAllPriceOfServiceByServiceIdAndRangeDate(serviceId, startDate.atTime(LocalTime.MIN), endDate.atTime(LocalTime.MAX));
                analyticServicePageResponse.setTotalPrice(totalPriceOfService);

                int totalNumberOfSold = orderItemRepository.countAllServiceTransitionByServiceIdAndRangeDate(serviceId, startDate.atTime(LocalTime.MIN), endDate.atTime(LocalTime.MAX));
                analyticServicePageResponse.setNumberOfSold(totalNumberOfSold);

                listAnalyticServicePageResponse.add(analyticServicePageResponse);
            }
        }

        int analyticServicePageResponseSize = listAnalyticServicePageResponse.size();
        //paging
        int start = (page - 1) * size;
        int end = start + size > analyticServicePageResponseSize ? analyticServicePageResponseSize : start + size;

        List<AnalyticServicePageResponse> pagedData;
        try {
            pagedData = listAnalyticServicePageResponse.subList(start, end);
        } catch (Exception ex) {
            pagedData = new ArrayList<>();
        }

        Map<String, Object> res = new HashMap<>();
        res.put("data", pagedData);
        res.put("totalPage", Math.ceil((double) analyticServicePageResponseSize / size));
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    public ResponseEntity<?> getAnalyticOverview(HttpServletRequest request, int dayAgo) {

        String role = authorizationUtil.getRoleFromAuthorizationHeader(request);
        if (!role.equals(Role.ADMIN.name())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient authority");
        }

        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(dayAgo);
        LocalDateTime startDateBefore = startDate.minusDays(dayAgo);

        int currentAllTransition = orderRepository.countOrderFromDateInputToDateInput(startDate, endDate);
        int beforeAllTransition = orderRepository.countOrderFromDateInputToDateInput(startDateBefore, startDate);

        double percentAllTransition;
        if (beforeAllTransition != 0) {
            percentAllTransition = ((double) (currentAllTransition - beforeAllTransition) / beforeAllTransition);
        } else {
            percentAllTransition = currentAllTransition;
        }
        percentAllTransition *= 100;

        double currentAllOrderPrice = orderRepository.sumOrderPriceFromDateInputToDateInput(startDate, endDate);
        double beforeAllOrderPrice = orderRepository.sumOrderPriceFromDateInputToDateInput(startDateBefore, startDate);

        double percentAllOrderPrice;
        if (beforeAllOrderPrice != 0) {
            percentAllOrderPrice = ((double) (currentAllOrderPrice - beforeAllOrderPrice) / beforeAllOrderPrice);
        } else {
            percentAllOrderPrice = currentAllOrderPrice;
        }
        percentAllOrderPrice *= 100;

        int totalCustomer = userRepository.countAllUser();

        RunReportRequest reportRequest = RunReportRequest.newBuilder()
                .setProperty("properties/" + propertyId)
                .addMetrics(Metric.newBuilder().setName("newUsers"))
                .addDateRanges(DateRange.newBuilder().setStartDate(startDate.toLocalDate().toString()).setEndDate(endDate.toLocalDate().toString()))
                .addDateRanges(DateRange.newBuilder().setStartDate(startDateBefore.toLocalDate().toString()).setEndDate(startDate.toLocalDate().toString()))
                .build();

        RunReportResponse response = analyticsData.runReport(reportRequest);

        int currentAllNewGuest = Integer.parseInt(response.getRowsList().get(1).getMetricValues(0).getValue());
        int beforeAllNewGuest = Integer.parseInt(response.getRowsList().get(0).getMetricValues(0).getValue());
        double percentAllNewGuest;
        if (beforeAllOrderPrice != 0) {
            percentAllNewGuest = ((double) (currentAllNewGuest - beforeAllNewGuest) / beforeAllNewGuest);
        } else {
            percentAllNewGuest = currentAllNewGuest;
        }
        percentAllNewGuest *= 100;

        AnalyticOverviewResponse analyticOverviewResponse = new AnalyticOverviewResponse();
        analyticOverviewResponse.setBeforeAllOrderPrice(beforeAllOrderPrice);
        analyticOverviewResponse.setBeforeAllTransition(beforeAllTransition);
        analyticOverviewResponse.setCurrentAllOrderPrice(currentAllOrderPrice);
        analyticOverviewResponse.setCurrentAllTransition(currentAllTransition);
        analyticOverviewResponse.setPercentAllTransition(percentAllTransition);
        analyticOverviewResponse.setPercentAllOrderPrice(percentAllOrderPrice);
        analyticOverviewResponse.setTotalCustomer(totalCustomer);
        analyticOverviewResponse.setCurrentAllNewGuest(currentAllNewGuest);
        analyticOverviewResponse.setBeforeAllNewGuest(beforeAllNewGuest);
        analyticOverviewResponse.setPercentAllNewGuest(percentAllNewGuest);

        return ResponseEntity.status(HttpStatus.OK).body(analyticOverviewResponse);

    }

    public ResponseEntity<?> getAnalyticRevenue(HttpServletRequest request, int dayAgo) {

        String role = authorizationUtil.getRoleFromAuthorizationHeader(request);
        if (!role.equals(Role.ADMIN.name())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient authority");
        }

        AnalyticRevenueResponse analyticRevenueResponse = new AnalyticRevenueResponse();
        analyticRevenueResponse.setBefore(new ArrayList<>());
        analyticRevenueResponse.setCurrent(new ArrayList<>());

        LocalDate currentDate = LocalDate.now();
        for (int currentDayAgo = 0, beforeDayAgo = dayAgo + 1; currentDayAgo <= dayAgo; currentDayAgo++, beforeDayAgo++) {

            AllOrderPrice allOrderPriceCurrent = analyticRevenueResponse.new AllOrderPrice();
            AllOrderPrice allOrderPriceBefore = analyticRevenueResponse.new AllOrderPrice();

            LocalDate startDateCurrent = currentDate.minusDays(currentDayAgo);
            LocalDate startDateBefore = currentDate.minusDays(beforeDayAgo);

            double currentAllOrderPrice = orderRepository.sumOrderPriceFromDateInputToDateInput(startDateCurrent.atStartOfDay(), startDateCurrent.atTime(LocalTime.MAX));
            double beforeAllOrderPrice = orderRepository.sumOrderPriceFromDateInputToDateInput(startDateBefore.atStartOfDay(), startDateBefore.atTime(LocalTime.MAX));

            double percentAllOrderPriceCurrent;
            if (beforeAllOrderPrice != 0) {
                percentAllOrderPriceCurrent = ((double) (currentAllOrderPrice - beforeAllOrderPrice) / beforeAllOrderPrice);
            } else {
                percentAllOrderPriceCurrent = currentAllOrderPrice;
            }
            percentAllOrderPriceCurrent *= 100;

            double percentAllOrderPriceBefore;
            if (currentAllOrderPrice != 0) {
                percentAllOrderPriceBefore = ((double) (beforeAllOrderPrice - currentAllOrderPrice) / currentAllOrderPrice);
            } else {
                percentAllOrderPriceBefore = beforeAllOrderPrice;
            }
            percentAllOrderPriceBefore *= 100;

            allOrderPriceCurrent.setDate(startDateCurrent);
            allOrderPriceCurrent.setAllOrderPrice(currentAllOrderPrice);
            allOrderPriceCurrent.setPercentAllOrderPrice(percentAllOrderPriceCurrent);

            allOrderPriceBefore.setDate(startDateBefore);
            allOrderPriceBefore.setAllOrderPrice(beforeAllOrderPrice);
            allOrderPriceBefore.setPercentAllOrderPrice(percentAllOrderPriceBefore);

            analyticRevenueResponse.getBefore().add(allOrderPriceBefore);
            analyticRevenueResponse.getCurrent().add(allOrderPriceCurrent);

        }

        return ResponseEntity.status(HttpStatus.OK).body(analyticRevenueResponse);

    }

    public ResponseEntity<?> getAnalyticCustomer(HttpServletRequest request, AnalyticPageDTO analyticPageDTO, String searchCustomerName, SortEnum sortTotalOrderPrice, SortEnum sortNumberOfOrder) {
        String role = authorizationUtil.getRoleFromAuthorizationHeader(request);
        if (!role.equals(Role.ADMIN.name())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient authority");
        }

        LocalDate startDate = analyticPageDTO.getStartDate();
        LocalDate endDate = analyticPageDTO.getEndDate();
        if (startDate == null || endDate == null) {
            startDate = LocalDate.of(1, 1, 1);
            endDate = LocalDate.now();
        }

        int size = analyticPageDTO.getSize();
        int page = analyticPageDTO.getPage();
        PageRequest pageRequest = PageRequest.of(page - 1, size);

        List<AnalyticCustomerResponse> listAnalyticCustomerResponse = new ArrayList<>();

        List<UserAccount> listCustomer;
        Page<UserAccount> paginatedListCustomer;
        if (searchCustomerName == null) {
            paginatedListCustomer = userRepository.getAllUserByUserRoleAndStartDateToEndDate(Role.CUSTOMER, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX), pageRequest);
        } else {
            paginatedListCustomer = userRepository.getAllUserByFullName(searchCustomerName, pageRequest);
        }
        listCustomer = paginatedListCustomer.getContent();

        for (UserAccount customer : listCustomer) {

            AnalyticCustomerResponse analyticCustomerResponse = new AnalyticCustomerResponse();
            analyticCustomerResponse.setUserName(customer.getFullName());
            analyticCustomerResponse.setDate(customer.getCreatedAt().toLocalDate());
            analyticCustomerResponse.setAvatar(customer.getAvatar());

            int customerId = customer.getUserId();
            analyticCustomerResponse.setUserId(customerId);

            int numberOfSchedule = scheduleRepository.countAllScheduleByUserId(customerId);
            analyticCustomerResponse.setNumberOfSchedule(numberOfSchedule);

            double totalOrderPrice = 0;

            List<Order> listOrder = orderRepository.getAllOrderCompleteByUserId(customerId);
            for (Order order : listOrder) {
                totalOrderPrice += order.getFinalPrice();
            }
            analyticCustomerResponse.setAmountSpent(totalOrderPrice);
            analyticCustomerResponse.setNumberOfOrder(listOrder.size());

            listAnalyticCustomerResponse.add(analyticCustomerResponse);

        }

        if (sortTotalOrderPrice != null) {
            if (sortTotalOrderPrice.equals(SortEnum.ASC)) {
                Collections.sort(listAnalyticCustomerResponse, Comparator.comparingDouble(AnalyticCustomerResponse::getAmountSpent));
            } else {
                Collections.sort(listAnalyticCustomerResponse, Comparator.comparingDouble(AnalyticCustomerResponse::getAmountSpent).reversed());
            }

        } else if (sortNumberOfOrder != null) {
            if (sortTotalOrderPrice.equals(SortEnum.ASC)) {
                Collections.sort(listAnalyticCustomerResponse, Comparator.comparingInt(AnalyticCustomerResponse::getNumberOfOrder));
            } else {
                Collections.sort(listAnalyticCustomerResponse, Comparator.comparingInt(AnalyticCustomerResponse::getNumberOfOrder).reversed());
            }
        }
        int totalPage = paginatedListCustomer.getTotalPages();
        Map<String, Object> res = new HashMap<>();
        res.put("data", listAnalyticCustomerResponse);
        res.put("totalPage", totalPage);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}
