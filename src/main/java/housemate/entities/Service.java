/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.entities;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;
import org.springframework.ui.context.ThemeSource;

import housemate.constants.SaleStatus;
import housemate.constants.UnitOfMeasure;
import housemate.repositories.ServiceRepository;
import housemate.services.TheService;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author ThanhF
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Configurable(preConstruction = true)
@Table(name = "Service")


public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(value = AccessLevel.NONE)
    @Column(name = "service_id")
    private int id;

    @Column(name = "title_name", unique = true)
    private String titleName;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit_of_measure", nullable = false)
    private UnitOfMeasure unitOfMeasure;

    @Column(name = "original_price", nullable = false)
    private int original_price;
    
    @Column(name = "sale_price", nullable = false)
    private int salePrice;
    
    @Column(name = "description", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "sale_status", nullable = false)
    private SaleStatus saleStatus;

    
    @Column(name = "avg_rating", nullable = true, columnDefinition = "float default 0")
    private Float avg_rating;

    //not yet building relationship
    @Column(name = "creator_id", nullable = false) 
    private int creatorId;

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;
    
    @Transient
    private int number_of_sold;
    


}


