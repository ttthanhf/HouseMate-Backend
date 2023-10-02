/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.entities;

import java.time.LocalDateTime;
import java.util.List;


import com.fasterxml.jackson.annotation.JsonIgnore;
import housemate.constants.Enum.SaleStatus;
import housemate.constants.Enum.UnitOfMeasure;
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

    @Column(name = "title_name", unique = true, nullable = false)
    private String titleName;



    @Column(name = "original_price", nullable = false)
    private int originalPrice;
    
    @Column(name = "sale_price", nullable = false)
    private int salePrice;
    

    @Enumerated(EnumType.STRING)
    @Column(name = "unit_of_measure", nullable = false)
    private UnitOfMeasure unitOfMeasure;
    


    @Column(name = "description", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "sale_status", nullable = false)
    private SaleStatus saleStatus;

    

    @Column(name = "avg_rating", columnDefinition = "float default 0")


    private Float avgRating;

    //not yet building relationship
    @Column(name = "creator_id", nullable = false) 
    private int creatorId;

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;
    

    @Column(name = "number_of_sold", columnDefinition = "integer default 0")
    private Integer numberOfSold;
    
    //This service is bought by many orders which construct service in order = service order item 



    @OneToMany(mappedBy = "service")
    @JsonIgnore
    private List<ServiceOrderItem> serviceOrderItemList;
    
    @OneToMany(mappedBy = "service")
    @JsonIgnore
    private List<PackageServiceItem> packageServiceItemList;



}


