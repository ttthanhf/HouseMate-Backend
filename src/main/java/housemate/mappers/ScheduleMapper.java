package housemate.mappers;

import housemate.constants.ScheduleStatus;
import housemate.entities.Schedule;
import housemate.entities.Service;
import housemate.models.DeliveryScheduleDTO;
import housemate.models.HourlyScheduleDTO;
import housemate.models.ReturnScheduleDTO;
import housemate.responses.EventRes;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class ScheduleMapper {
    public Schedule mapToEntity(HourlyScheduleDTO hourlyScheduleDTO) {
        Schedule schedule = new Schedule();
        LocalDateTime startDate = hourlyScheduleDTO.getDate().atTime(hourlyScheduleDTO.getTimeRanges().get(0));
        LocalDateTime endDate = hourlyScheduleDTO.getDate().atTime(hourlyScheduleDTO.getTimeRanges().get(1));

        schedule.setCycle(hourlyScheduleDTO.getCycle());
        schedule.setStartDate(startDate);
        schedule.setEndDate(endDate);
        schedule.setNote(hourlyScheduleDTO.getNote());
        schedule.setServiceId(hourlyScheduleDTO.getServiceId());
        schedule.setServiceTypeId(hourlyScheduleDTO.getTypeId());
        schedule.setStatus(ScheduleStatus.PROCESSING);
        schedule.setQuantityRetrieve(1);

        return schedule;
    }

    public List<Schedule> mapToEntity(ReturnScheduleDTO returnScheduleDTO) {
        List<Schedule> schedules = new ArrayList<>();

        // Pickup schedule
        Schedule pickupSchedule = getSchedule(returnScheduleDTO, returnScheduleDTO.getPickupDate(), returnScheduleDTO.getTime());
        schedules.add(pickupSchedule);

        // Received schedule
        Schedule receivedSchedule = getSchedule(returnScheduleDTO, returnScheduleDTO.getReceivedDate(), returnScheduleDTO.getReceivedTime());
        schedules.add(receivedSchedule);

        return schedules;
    }

    public Schedule mapToEntity(DeliveryScheduleDTO deliveryScheduleDTO) {
        Schedule schedule = new Schedule();
        LocalDateTime startDate = deliveryScheduleDTO.getDate().atTime(deliveryScheduleDTO.getTime());

        schedule.setCycle(deliveryScheduleDTO.getCycle());
        schedule.setStartDate(startDate);
        schedule.setEndDate(startDate.plusHours(1)); // Default endTime is add 1hr in startDate
        schedule.setNote(deliveryScheduleDTO.getNote());
        schedule.setQuantityRetrieve(deliveryScheduleDTO.getQuantity());
        schedule.setServiceId(deliveryScheduleDTO.getServiceId());
        schedule.setServiceTypeId(deliveryScheduleDTO.getTypeId());
        schedule.setStatus(ScheduleStatus.PROCESSING);

        return schedule;
    }

    public EventRes mapToEventRes(Schedule schedule, Service service) {
        EventRes event = new EventRes();

        event.setTitle(service.getTitleName());
        event.setStart(schedule.getStartDate());
        event.setEnd(schedule.getEndDate());
        event.setStatus(schedule.getStatus());

        return event;
    }

    // Reusable function
    private static Schedule getSchedule(ReturnScheduleDTO returnScheduleDTO, LocalDate returnDate, LocalTime returnTime) {
        Schedule schedule = new Schedule();
        schedule.setCycle(returnScheduleDTO.getCycle());
        schedule.setNote(returnScheduleDTO.getNote());
        schedule.setServiceId(returnScheduleDTO.getServiceId());
        schedule.setServiceTypeId(returnScheduleDTO.getTypeId());
        schedule.setStatus(ScheduleStatus.PROCESSING);
        schedule.setQuantityRetrieve(1);

        LocalDateTime pickupDateTime = returnDate.atTime(returnTime);
        schedule.setStartDate(pickupDateTime);
        schedule.setEndDate(pickupDateTime.plusHours(1));
        return schedule;
    }

}
