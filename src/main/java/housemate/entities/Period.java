/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.entities;

import housemate.constants.Enum.UnitOfMeasure;
import jakarta.persistence.*;
import lombok.Data;

/**
 *
 * @author ThanhF
 */
@Entity
@Data
@Table(name = "period_service")
public class Period {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "period_id")
    private int periodId;

    @Column(name = "period_name")
    private String periodName;

    @Column(name = "percent")
    private float percent;
    
    @Column(name = "period_value")
    private int value;

    @Transient
    private int finalPrice;

    @Transient
    private int originalPrice;
}
