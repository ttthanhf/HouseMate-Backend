/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author ThanhF
 */
@Data
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private int orderItemId;

    @Column(name = "order_id")
    private int orderId;

    @Column(name = "service_id")
    private int serviceId;

    @Column(name = "expire_date")
    private LocalDateTime expireDate;

    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "final_price")
    private int finalPrice;

    @Column(name = "original_price")
    private int originalPrice;

    @Column(name = "period_name")
    private String periodName;

    @Transient
    private int discountPrice;

    @OneToOne
    @JoinColumn(name = "service_id", updatable = false, insertable = false)
    private Service service;
}
