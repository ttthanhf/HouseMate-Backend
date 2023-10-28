package housemate.mappers;

import housemate.constants.ScheduleStatus;
import housemate.entities.Schedule;
import housemate.entities.Service;
import housemate.models.ScheduleDTO;
import housemate.responses.EventRes;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class ScheduleMapper {
    public Schedule mapToEntity(ScheduleDTO scheduleDTO) {
        Schedule schedule = new Schedule();
        String note = scheduleDTO.getNote();
        int quantity = scheduleDTO.getQuantityRetrieve();
        LocalDateTime startDate = scheduleDTO.getStartDate();
        LocalDateTime endDate = scheduleDTO.getEndDate();

        schedule.setServiceId(scheduleDTO.getServiceId());
        schedule.setServiceTypeId(scheduleDTO.getTypeId());
        schedule.setQuantityRetrieve(quantity == 0 ? (int) Duration.between(startDate, endDate).toHours() : quantity);
        schedule.setStartDate(startDate);
        schedule.setEndDate(endDate);
        schedule.setNote(note == null ? "" : note.trim());
        schedule.setCycle(scheduleDTO.getCycle());
        schedule.setStatus(ScheduleStatus.PROCESSING);
        schedule.setUserUsageId(schedule.getUserUsageId());

        return schedule;
    }

    public EventRes mapToEventRes(Schedule schedule, Service service) {
        EventRes event = new EventRes();
        boolean isBeforeCurrentDate = schedule.getEndDate().isBefore(LocalDateTime.now());

        event.setTitle(service.getTitleName());
        event.setStart(schedule.getStartDate());
        event.setEnd(schedule.getEndDate());
        event.setStatus(isBeforeCurrentDate ? ScheduleStatus.CANCEL : schedule.getStatus());

        return event;
    }

}
