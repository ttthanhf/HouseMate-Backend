/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.services;

import housemate.entities.Service;
import housemate.models.CleanlinessScheduleDTO;
import housemate.models.DeliveryScheduleDTO;
import housemate.models.HourlyScheduleDTO;
import housemate.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * @author ThanhF
 */
@org.springframework.stereotype.Service
public class ScheduleService {

    @Autowired
    ServiceRepository serviceRepository;

    public ResponseEntity<String> createHourlySchedule(HourlyScheduleDTO createHourlySchedule) {
        return ResponseEntity.status(HttpStatus.OK).body("Doing...");
    }

    public ResponseEntity<String> createCleanlinessSchedule(CleanlinessScheduleDTO scheduleDTO) {
        ResponseEntity<String> validation = validateServiceIdAndDate(scheduleDTO.getServiceId(), scheduleDTO.getCleanDate());
        if (validation != null) return validation;

        return ResponseEntity.status(HttpStatus.OK).body("Doing...");
    }

    public ResponseEntity<String> createDeliverySchedule(DeliveryScheduleDTO deliveryScheduleDTO) {
        return ResponseEntity.status(HttpStatus.OK).body("Doing...");
    }

    private ResponseEntity<String> validateServiceIdAndDate(int serviceId, Date date) {
        // Validate service ID
        Service service = serviceRepository.getServiceByServiceId(serviceId);
        if (service == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Can't find this service ID");
        }

        // Validate schedule >= current + 3
        Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        long currentDate = new Date().getTime();
        long scheduleDate = date.getTime();
        int MINIMUM_HOURS = 3;
        if (scheduleDate - currentDate >= MINIMUM_HOURS * 60 * 60 * 1000) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You must set your schedule after " + currentDate + MINIMUM_HOURS * 60 * 60 * 1000);
        }

        return null;
    }

}
