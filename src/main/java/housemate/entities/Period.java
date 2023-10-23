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

/**
 *
 * @author ThanhF
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "period_service")
public class Period {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "period_id")
    private int periodId;

    @Column(name = "service_id")
    private int serviceId;

    @Column(name = "period_value")
    private int periodValue;

    @Column(name = "period_name")
    private String periodName;

    @Column(name = "final_price")
    private int finalPrice;
    
    @Column(name = "original_price")
    private int originalPrice;
    
    @Column(name = "percent")
    private float percent;

}
