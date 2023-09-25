/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.entities;

import jakarta.persistence.*;

/**
 *
 * @author ThanhF
 */
@Entity
@Table(name = "Service_Cart_Item")
public class ServiceCartItem {

    @Id
    @Column(name = "cart_id")
    private int id;

    @Column(name = "customer_id")
    private int customerId;

    @Column(name = "service_id")
    private int serviceId;

    @Column(name = "package_id")
    private int packageId;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "added_at")
    private int addedAt;

    public ServiceCartItem(int id, int customerId, int serviceId, int packageId, int quantity, int addedAt) {
        this.id = id;
        this.customerId = customerId;
        this.serviceId = serviceId;
        this.packageId = packageId;
        this.quantity = quantity;
        this.addedAt = addedAt;
    }

    
}

