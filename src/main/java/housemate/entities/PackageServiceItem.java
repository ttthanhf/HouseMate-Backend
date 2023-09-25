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
@Table(name = "Package_Service_Item")
public class PackageServiceItem {

    @Id
    @Column(name = "package_service_id")
    private int packageServiceId;

    @Id
    @Column(name = "service_id")
    private int serviceId;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "date_start")
    private Date dateStart;

    @Column(name = "date_end")
    private Date dateEnd;

    public PackageServiceItem(int packageServiceId, int serviceId, int quantity, Date dateStart, Date dateEnd) {
        this.packageServiceId = packageServiceId;
        this.serviceId = serviceId;
        this.quantity = quantity;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
    }

}
