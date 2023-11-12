/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.SneakyThrows;

/**
 *
 * @author ThanhF
 */
@Data
@Entity
@Table(name = "user_usage")
public class UserUsage implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_usage_id")
    private int userUsageId;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "service_id")
    private int serviceId;

    @Column(name = "remaining")
    private int remaining;

    @Column(name = "total")
    private int total;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "order_item_id")
    private int orderItemId;

    @Column(name = "is_expired")
    private boolean isExpired;
    
    @Column(name = "quantity_over")
    private int qtyOver;
    
    @SneakyThrows
    @Override
    public UserUsage clone() {
        return (UserUsage) super.clone();
    }

    @Transient
    private Service service;
}
