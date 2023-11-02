/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.entities;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.List;
import lombok.Data;

/**
 *
 * @author ThanhF
 */
@Entity
@Table(name = "carts")
@Data
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private int cartId;

    @Column(name = "user_id")
    @Hidden
    private int userId;

    @Column(name = "service_id")
    private int serviceId;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "period_id")
    private int periodId;

    @Transient
    private int finalPrice;

    @Transient
    private int originalPrice;

    @Transient
    private List<Period> listPeriod;

    @Transient
    private Service service;

}
