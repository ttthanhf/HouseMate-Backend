/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.entities;

import housemate.constants.PaymentMethod;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
 *
 * @author ThanhF
 */
@Data
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private int orderId;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "payment_method")
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(name = "is_complete")
    private boolean isComplete;

    @Column(name = "final_price")
    private int finalPrice;

    @Column(name = "sub_total")
    private int subTotal;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "transaction_date")
    private String transactionDate;
    
    @Column(name = "address")
    private String address;

    @Transient
    private int discountPrice;

    @Transient
    private UserAccount user;

    @Transient
    private List<OrderItem> listOrderItem;

}
