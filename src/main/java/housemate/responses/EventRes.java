package housemate.responses;

import housemate.constants.ScheduleStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventRes {
    private int scheduleId;
    private String title;
    private LocalDateTime start;
    private LocalDateTime end;
    private ScheduleStatus status;
    private String name;
    private String phone;
}
