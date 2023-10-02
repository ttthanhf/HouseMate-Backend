/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.entities;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author ThanhF
 */
@Entity
@Table(name = "carts")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private int id;

    @Column(name = "user_id")
    @Hidden
    private int userId;

    @Column(name = "service_id")
    private int serviceId;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "date")
    private LocalDateTime date;
}
