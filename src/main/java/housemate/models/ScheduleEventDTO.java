package housemate.models;


import housemate.constants.Status;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleEventDTO {
    private String title;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status;
    private String staff;
    private String phone;
}
