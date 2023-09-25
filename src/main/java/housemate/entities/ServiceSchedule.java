/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.entities;

import jakarta.persistence.*;
import java.util.Date;

/**
 *
 * @author ThanhF
 */
@Entity
@Table(name = "Service_Schedule")
public class ServiceSchedule {

    @Id
    @Column(name = "service_schedule_id")
    private int id;

    @Column(name = "quantity_retrieve")
    private int quantityRetrieve;

    @Column(name = "date_start")
    private int dateStart;

    @Column(name = "date_end")
    private int dateEnd;

    @Column(name = "home_address_id")
    private int homeAddressId;

    @Column(name = "status")
    private String status;

    @Column(name = "scheduled_at")
    private Date scheduledAt;

    @Column(name = "note")
    private String note;

    public ServiceSchedule(int id, int quantityRetrieve, int dateStart, int dateEnd, int homeAddressId, String status, Date scheduledAt, String note) {
        this.id = id;
        this.quantityRetrieve = quantityRetrieve;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.homeAddressId = homeAddressId;
        this.status = status;
        this.scheduledAt = scheduledAt;
        this.note = note;
    }

    
}
