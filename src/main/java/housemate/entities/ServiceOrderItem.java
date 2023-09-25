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
@Table(name = "Service_Order_Item")
public class ServiceOrderItem {

    @Id
    @Column(name = "service_order_item_id")
    private int id;

    @Column(name = "order_id")
    private int orderId;

    @Column(name = "service_id")
    private int serviceId;

    @Column(name = "package_service_id")
    private int packageServiceId;

    @Column(name = "quantity_purchased")
    private int quantityPurchased;

    @Column(name = "sub_total")
    private int subTotal;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    public ServiceOrderItem(int id, int orderId, int serviceId, int packageServiceId, int quantityPurchased, int subTotal, Date startDate, Date endDate) {
        this.id = id;
        this.orderId = orderId;
        this.serviceId = serviceId;
        this.packageServiceId = packageServiceId;
        this.quantityPurchased = quantityPurchased;
        this.subTotal = subTotal;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}

