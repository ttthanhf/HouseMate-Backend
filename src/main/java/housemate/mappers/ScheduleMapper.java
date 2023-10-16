package housemate.mappers;

import housemate.constants.Status;
import housemate.entities.Schedule;
import housemate.models.DeliveryScheduleDTO;
import housemate.models.HourlyScheduleDTO;
import housemate.models.ReturnScheduleDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ScheduleMapper {
    public Schedule mapToEntity(HourlyScheduleDTO hourlyScheduleDTO) {
        Schedule schedule = new Schedule();
        return null;
    }

    public Schedule mapToEntity(ReturnScheduleDTO returnScheduleDTO) {
        Schedule schedule = new Schedule();
        return null;
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
        schedule.setStatus(Status.PENDING);

        return schedule;
    }
}
