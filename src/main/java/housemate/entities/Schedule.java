package housemate.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 *
 * @author hdang09
 */
@Entity
@Table(name = "schedule")
@Data
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private int scheduleId;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "note")
    private String note;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "service_id")
    private int serviceId;

    @Column(name = "type_id")
    private int typeId;

    @Column(name = "customer_id")
    private int customerId;

    @Column(name = "staff_id")
    private int staffId;

    @Column(name = "status")
    private String status;
}
