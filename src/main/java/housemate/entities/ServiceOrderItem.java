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

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 *
 * @author ThanhF
 */
@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
@Table(name = "service_order_item")
public class ServiceOrderItem {

    @Id
    @Column(name = "service_order_item_id")
    private int serviceOrderItemId;

    @ManyToOne
    @JoinColumn(name ="order_id", insertable = false, updatable = false)
    private ServiceOrder serviceOrder;

    @ManyToOne
    @JoinColumn(name = "service_id", insertable = false, updatable = false)
    private Service service;

    @ManyToOne
    @JoinColumn(name = "package_service_id", insertable = false, updatable = false)
    private PackageService packageService;


    @Column(name = "quantity_purchased")
    private int quantityPurchased;

    @Column(name = "sub_total")
    private int subTotal;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

   
}

