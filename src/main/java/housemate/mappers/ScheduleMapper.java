package housemate.mappers;

import housemate.constants.ScheduleStatus;
import housemate.entities.Schedule;
import housemate.entities.Service;
import housemate.models.DeliveryScheduleDTO;
import housemate.models.HourlyScheduleDTO;
import housemate.models.ReturnScheduleDTO;
import housemate.responses.EventRes;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class ScheduleMapper {
    public Schedule mapToEntity(HourlyScheduleDTO hourlyScheduleDTO) {
        Schedule schedule = new Schedule();
        LocalDateTime startDate = hourlyScheduleDTO.getDate().atTime(hourlyScheduleDTO.getTimeRanges().get(0));
        LocalDateTime endDate = hourlyScheduleDTO.getDate().atTime(hourlyScheduleDTO.getTimeRanges().get(1));
        int quantityRetrieve = (int) Duration.between(startDate, endDate).toHours();
        String note = hourlyScheduleDTO.getNote();

        schedule.setServiceId(hourlyScheduleDTO.getServiceId());
        schedule.setServiceTypeId(hourlyScheduleDTO.getTypeId());
        schedule.setQuantityRetrieve(quantityRetrieve);
        schedule.setStartDate(startDate);
        schedule.setEndDate(endDate);
        schedule.setNote(note == null ? "" : note.trim());
        schedule.setCycle(hourlyScheduleDTO.getCycle());
        schedule.setStatus(ScheduleStatus.PROCESSING);

        return schedule;
    }

    public Schedule mapToEntity(ReturnScheduleDTO returnScheduleDTO) {
        Schedule schedule = new Schedule();
        LocalDateTime pickupDateTime = returnScheduleDTO.getPickUpDate().atTime(returnScheduleDTO.getTime());
        LocalDateTime receivedDateTime = returnScheduleDTO.getReceiveDate().atTime(returnScheduleDTO.getReceivedTime());
        String note = returnScheduleDTO.getNote();

        schedule.setServiceId(returnScheduleDTO.getServiceId());
        schedule.setServiceTypeId(returnScheduleDTO.getTypeId());
        schedule.setQuantityRetrieve(1);
        schedule.setStartDate(pickupDateTime);
        schedule.setEndDate(receivedDateTime);
        schedule.setNote(note == null ? "" : note.trim());
        schedule.setCycle(returnScheduleDTO.getCycle());
        schedule.setStatus(ScheduleStatus.PROCESSING);

        return schedule;
    }

    public Schedule mapToEntity(DeliveryScheduleDTO deliveryScheduleDTO) {
        Schedule schedule = new Schedule();
        LocalDateTime startDate = deliveryScheduleDTO.getDate().atTime(deliveryScheduleDTO.getTime());
        String note = deliveryScheduleDTO.getNote();

        schedule.setServiceId(deliveryScheduleDTO.getServiceId());
        schedule.setServiceTypeId(deliveryScheduleDTO.getTypeId());
        schedule.setQuantityRetrieve(deliveryScheduleDTO.getQuantity());
        schedule.setStartDate(startDate);
        schedule.setEndDate(startDate.plusHours(1)); // Default endTime is add 1hr in startDate
        schedule.setNote(note == null ? "" : note.trim());
        schedule.setCycle(deliveryScheduleDTO.getCycle());
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

}
