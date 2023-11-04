package housemate.mappers;

import housemate.constants.ScheduleStatus;
import housemate.entities.Schedule;
import housemate.entities.Service;
import housemate.models.ScheduleDTO;
import housemate.responses.EventRes;
import org.springframework.stereotype.Component;

@Component
public class ScheduleMapper {
    public Schedule mapToEntity(ScheduleDTO scheduleDTO) {
        Schedule schedule = new Schedule();
        String note = scheduleDTO.getNote();

        schedule.setServiceId(scheduleDTO.getServiceId());
        schedule.setServiceTypeId(scheduleDTO.getTypeId());
        schedule.setQuantityRetrieve(scheduleDTO.getQuantityRetrieve());
        schedule.setStartDate(scheduleDTO.getStartDate());
        schedule.setEndDate(scheduleDTO.getEndDate());
        schedule.setNote(note == null ? "" : note.trim());
        schedule.setCycle(scheduleDTO.getCycle());
        schedule.setStatus(ScheduleStatus.PROCESSING);
        schedule.setUserUsageId(schedule.getUserUsageId());

        return schedule;
    }

    public EventRes mapToEventRes(Schedule schedule, Service service) {
        EventRes event = new EventRes();

        event.setScheduleId(schedule.getScheduleId());
        event.setTitle(service.getTitleName());
        event.setStart(schedule.getStartDate());
        event.setEnd(schedule.getEndDate());
        event.setStatus(schedule.getStatus());

        return event;
    }

}
