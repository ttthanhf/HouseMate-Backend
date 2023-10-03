/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 *
 * @author Anh
 */
@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
@Table(name = "service_order")
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

    @OneToMany(mappedBy = "serviceOrder")
    private List<ServiceOrderItem> serviceOrderItemList;

    
}
