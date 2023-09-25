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
@Table(name = "Service_Order")
public class ServiceOrder {

    @Id
    @Column(name = "order_id")
    private int orderId;

    @Column(name = "customer_id")
    private int customerId;

    @Column(name = "order_date")
    private Date orderDate;

    @Column(name = "total_amount")
    private int totalAmount;

    @Column(name = "discount")
    private Integer discount;

    @Column(name = "final_total_payment")
    private int finalTotalPayment;

    @Column(name = "status")
    private String status;

    @Column(name = "payment_transaction_id")
    private int paymentTransactionId;

    public ServiceOrder(int orderId, int customerId, Date orderDate, int totalAmount, Integer discount, int finalTotalPayment, String status, int paymentTransactionId) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.discount = discount;
        this.finalTotalPayment = finalTotalPayment;
        this.status = status;
        this.paymentTransactionId = paymentTransactionId;
    }

    
}
