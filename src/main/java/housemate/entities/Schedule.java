package housemate.entities;

import housemate.constants.Cycle;
import housemate.constants.Status;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 *
 * @author hdang09
 */
@Entity
@Table(name = "service_schedule")
@Data
public class Schedule {

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

    @Column(name = "order_item_id")
    private int orderItemId;

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
    private Status status;
}
