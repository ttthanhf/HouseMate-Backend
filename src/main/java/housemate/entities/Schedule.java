package housemate.entities;

import housemate.constants.Cycle;
import housemate.constants.ScheduleStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.time.LocalDateTime;

/**
 * @author hdang09
 */
@Entity
@Table(name = "schedule")
@Data
@NoArgsConstructor
public class Schedule implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_schedule_id")
    private int scheduleId;

    @Column(name = "service_id")
    private int serviceId;

    @Column(name = "service_type_id")
    private int serviceTypeId;

    @Column(name = "customer_id")
    private int customerId;

    @Column(name = "staff_id")
    private int staffId;

    @Column(name = "quantity_retrieve")
    private int quantityRetrieve;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "note")
    private String note;

    @Column(name = "cycle_name")
    @Enumerated(EnumType.STRING)
    private Cycle cycle;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ScheduleStatus status;

    @Column(name = "parent_schedule_id")
    private int parentScheduleId;

    @Column(name = "user_usage_id")
    private int userUsageId;
    
    @Column(name = "on_task")
    private boolean onTask;

    @SneakyThrows
    @Override
    public Schedule clone() {
        return (Schedule) super.clone();
    }

    @Transient
    private String staff;

    @Transient
    private String phone;
}