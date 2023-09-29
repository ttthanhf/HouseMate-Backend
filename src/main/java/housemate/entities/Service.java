/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.entities;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.Formula;
import org.springframework.beans.factory.annotation.Configurable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import housemate.constants.SaleStatus;
import housemate.constants.UnitOfMeasure;
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
@Table(name = "service")


public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(value = AccessLevel.NONE)
    @Column(name = "service_id")
    private int serviceId;

    @Column(name = "title_name", unique = true)
    private String titleName;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit_of_measure", nullable = false)
    private UnitOfMeasure unitOfMeasure;

    @Column(name = "original_price", nullable = false)
    private int originalPrice;
    
    @Column(name = "sale_price", nullable = false)
    private int salePrice;
    
    @Column(name = "description", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "sale_status", nullable = false)
    private SaleStatus saleStatus;

    
    @Column(name = "avg_rating", nullable = true, columnDefinition = "float default 0")
    private Float avgRating;

    //not yet building relationship
    @Column(name = "creator_id", nullable = false) 
    private int creatorId;

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;
    
    //@Transient
    //@Formula("SELECT DISTINCT COUNT(soi.service_order_item_id) FROM service_order_item soi WHERE soi.service_id = service_id")
    @Column(name = "number_of_sold", columnDefinition = "integer default 0")
    private Integer numberOfSold;
    
    @OneToMany(mappedBy = "service")
    @JsonIgnore
    private List<ServiceOrderItem> serviceOrderItemList;
    
    


}


