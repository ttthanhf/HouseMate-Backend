package housemate.services;

import housemate.entities.Schedule;
import housemate.entities.Service;
import housemate.mappers.ScheduleMapper;
import housemate.models.DeliveryScheduleDTO;
import housemate.models.HourlyScheduleDTO;
import housemate.models.PurchasedServiceDTO;
import housemate.models.ReturnScheduleDTO;
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
import java.util.List;

/**
 * @author ThanhF
 */
@org.springframework.stereotype.Service
public class ScheduleService {

    ServiceRepository serviceRepository;
    ScheduleRepository scheduleRepository;
    ScheduleMapper scheduleMapper;
    AuthorizationUtil authorizationUtil;

    @Autowired
    public ScheduleService(ServiceRepository serviceRepository, ScheduleRepository scheduleRepository, ScheduleMapper scheduleMapper, AuthorizationUtil authorizationUtil) {
        this.serviceRepository = serviceRepository;
        this.scheduleRepository = scheduleRepository;
        this.scheduleMapper = scheduleMapper;
        this.authorizationUtil = authorizationUtil;
    }

    public ResponseEntity<List<PurchasedServiceDTO>> getAllPurchased(HttpServletRequest request) {
        List<PurchasedServiceDTO> services = new ArrayList<>();

        // TODO: Get all serviceID based on order item

        // TODO: Get all type list

        return ResponseEntity.status(HttpStatus.OK).body(services);
    }

    public ResponseEntity<String> createHourlySchedule(HttpServletRequest request, HourlyScheduleDTO scheduleDTO) {
        return ResponseEntity.status(HttpStatus.OK).body("Doing...");
    }

    public ResponseEntity<String> createReturnSchedule(HttpServletRequest request, ReturnScheduleDTO scheduleDTO) {
        return ResponseEntity.status(HttpStatus.OK).body("Doing...");
    }

    public ResponseEntity<String> createDeliverySchedule(HttpServletRequest request, DeliveryScheduleDTO scheduleDTO) {
        // Validate service ID and date
        LocalDateTime scheduleDateTime = scheduleDTO.getDate().atTime(scheduleDTO.getTime());
        ResponseEntity<String> validation = validateServiceIdAndDate(scheduleDTO.getServiceId(), scheduleDateTime);
        if (validation != null) return validation;

        Schedule schedule = scheduleMapper.mapToEntity(scheduleDTO);
        int customerId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
        schedule.setCustomerId(customerId);
        // TODO: Store orderItemId
        scheduleRepository.save(schedule);

        return ResponseEntity.status(HttpStatus.OK).body("Create successfully!");
    }

    private ResponseEntity<String> validateServiceIdAndDate(int serviceId, LocalDateTime dateTime) {
        // Validate service ID
        Service service = serviceRepository.getServiceByServiceId(serviceId);
        if (service == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Can't find this service ID");
        }

        long MINIMUM_HOURS = 3;
        LocalDateTime currentDate = LocalDateTime.now();

        // Validate schedule >= current + 3hr
        if (!dateTime.isAfter(currentDate.plusHours(MINIMUM_HOURS))) {
            String formattedDate = currentDate.plusHours(MINIMUM_HOURS).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You must set your schedule after " + formattedDate);
        }

        // Validate time in office hours
        int scheduleHour = dateTime.getHour();
        if (scheduleHour <= 5 || scheduleHour >= 19) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please set your time in range from 6:00 to 20:00");
        }

        return null;
    }

}
