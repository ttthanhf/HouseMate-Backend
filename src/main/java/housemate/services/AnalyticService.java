package housemate.services;

import com.google.analytics.data.v1beta.*;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import housemate.constants.Role;
import housemate.models.AnalyticDTO;
import housemate.models.responses.AnalyticPageResponse;
import housemate.models.responses.AnalyticUserResponse;
import housemate.utils.AuthorizationUtil;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AnalyticService implements DisposableBean {

    @Value("${google.analytic.property-id}")
    private String propertyId;

    @Value("${google.analytic.credentials.name}")
    private String credentialsPath;

    private GoogleCredentials credentials;
    private BetaAnalyticsDataSettings betaAnalyticsDataSettings;
    private BetaAnalyticsDataClient analyticsData;

    @Autowired
    private AuthorizationUtil authorizationUtil;

    @PostConstruct
    public void init() {
        try {
            credentials = GoogleCredentials.fromStream(new FileInputStream(getClass().getClassLoader().getResource(credentialsPath).getFile()));
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

    public ResponseEntity<?> getAnalyticUser(HttpServletRequest request, AnalyticDTO analyticDTO) {

        String role = authorizationUtil.getRoleFromAuthorizationHeader(request);
        if (!role.equals(Role.ADMIN.name())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient authority");
        }

        RunReportRequest reportRequest = RunReportRequest.newBuilder()
                .setProperty("properties/" + propertyId)
                .addDimensions(Dimension.newBuilder().setName("day"))
                .addDimensions(Dimension.newBuilder().setName("month"))
                .addDimensions(Dimension.newBuilder().setName("year"))
                .addMetrics(Metric.newBuilder().setName("sessions"))
                .addMetrics(Metric.newBuilder().setName("newUsers"))
                .addMetrics(Metric.newBuilder().setName("activeUsers"))
                .addDateRanges(DateRange.newBuilder().setStartDate(analyticDTO.getStartDate().toString()).setEndDate(analyticDTO.getEndDate().toString()))
                .build();

        RunReportResponse response = analyticsData.runReport(reportRequest);

        List<AnalyticUserResponse> listAnalyticUserResponse = new ArrayList<>();
        for (Row row : response.getRowsList()) {
            AnalyticUserResponse analyticUserResponse = new AnalyticUserResponse();
            analyticUserResponse.setDay(row.getDimensionValues(0).getValue());
            analyticUserResponse.setMonth(row.getDimensionValues(1).getValue());
            analyticUserResponse.setYear(row.getDimensionValues(2).getValue());
            analyticUserResponse.setSessions(row.getMetricValues(0).getValue());
            analyticUserResponse.setNewUsers(row.getMetricValues(1).getValue());
            analyticUserResponse.setActiveUsers(row.getMetricValues(2).getValue());
            listAnalyticUserResponse.add(analyticUserResponse);
        }

        Comparator<AnalyticUserResponse> comparator = Comparator
                .comparing(AnalyticUserResponse::getYear)
                .thenComparing(AnalyticUserResponse::getMonth)
                .thenComparing(AnalyticUserResponse::getDay);
        Collections.sort(listAnalyticUserResponse, comparator);

        return ResponseEntity.status(HttpStatus.OK).body(listAnalyticUserResponse);
    }

    public ResponseEntity<?> getAnalyticPage(HttpServletRequest request, AnalyticDTO analyticDTO) {

        String role = authorizationUtil.getRoleFromAuthorizationHeader(request);
        if (!role.equals(Role.ADMIN.name())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Insufficient authority");
        }

        RunReportRequest reportRequest = RunReportRequest.newBuilder()
                .setProperty("properties/" + propertyId)
                .addDimensions(Dimension.newBuilder().setName("day"))
                .addDimensions(Dimension.newBuilder().setName("month"))
                .addDimensions(Dimension.newBuilder().setName("year"))
                .addDimensions(Dimension.newBuilder().setName("pageTitle"))
                .addMetrics(Metric.newBuilder().setName("sessions"))
                .addMetrics(Metric.newBuilder().setName("newUsers"))
                .addMetrics(Metric.newBuilder().setName("activeUsers"))
                .addMetrics(Metric.newBuilder().setName("eventCount"))
                .addDateRanges(DateRange.newBuilder().setStartDate(analyticDTO.getStartDate().toString()).setEndDate(analyticDTO.getEndDate().toString()))
                .build();

        RunReportResponse response = analyticsData.runReport(reportRequest);

        List<AnalyticPageResponse> listAnalyticPageResponse = new ArrayList<>();
        for (Row row : response.getRowsList()) {
            AnalyticPageResponse analyticPageResponse = new AnalyticPageResponse();
            analyticPageResponse.setDay(row.getDimensionValues(0).getValue());
            analyticPageResponse.setMonth(row.getDimensionValues(1).getValue());
            analyticPageResponse.setYear(row.getDimensionValues(2).getValue());
            analyticPageResponse.setPageTitle(row.getDimensionValues(3).getValue());
            analyticPageResponse.setSessions(row.getMetricValues(0).getValue());
            analyticPageResponse.setNewUsers(row.getMetricValues(1).getValue());
            analyticPageResponse.setActiveUsers(row.getMetricValues(2).getValue());
            analyticPageResponse.setEventCount(row.getMetricValues(3).getValue());
            listAnalyticPageResponse.add(analyticPageResponse);
        }

        Comparator<AnalyticPageResponse> comparator = Comparator
                .comparing(AnalyticPageResponse::getYear)
                .thenComparing(AnalyticPageResponse::getMonth)
                .thenComparing(AnalyticPageResponse::getDay);
        Collections.sort(listAnalyticPageResponse, comparator);

        return ResponseEntity.status(HttpStatus.OK).body(listAnalyticPageResponse);
    }
}
