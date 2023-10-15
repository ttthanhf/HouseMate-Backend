/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.entities;

import jakarta.persistence.*;
import java.util.List;
import lombok.Data;

/**
 *
 * @author ThanhF
 */
@Entity
@Table(name = "Service")
@Data
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id")
    private int serviceId;

    @Column(name = "original_price")
    private int originalPriceService;

    @Column(name = "final_price")
    private int finalPriceService;

    @Column(name = "title_name")
    private String titleName;

    @Column(name = "number_of_sold")
    private int numberOfSold;

    @Column(name = "is_package")
    private boolean isPackage;

    @Transient
    private String image;

    @Transient
    private List<Period> listPeriodPrice;

}
