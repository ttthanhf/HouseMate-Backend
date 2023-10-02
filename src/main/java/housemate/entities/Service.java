/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.entities;

import java.time.LocalDateTime;
import java.util.List;

<<<<<<< HEAD

import com.fasterxml.jackson.annotation.JsonIgnore;
import housemate.constants.Enum.SaleStatus;
import housemate.constants.Enum.UnitOfMeasure;
=======
import org.hibernate.annotations.Formula;
import org.springframework.beans.factory.annotation.Configurable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import housemate.constants.SaleStatus;
import housemate.constants.UnitOfMeasure;
>>>>>>> 1af5ca64aeceb766f630c5431341dbd512787af3
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

<<<<<<< HEAD
=======
    @Enumerated(EnumType.STRING)
    @Column(name = "unit_of_measure", nullable = false)
    private UnitOfMeasure unitOfMeasure;
>>>>>>> 1af5ca64aeceb766f630c5431341dbd512787af3

    @Column(name = "original_price", nullable = false)
    private int originalPrice;
    
    @Column(name = "sale_price", nullable = false)
    private int salePrice;
    
<<<<<<< HEAD
    @Enumerated(EnumType.STRING)
    @Column(name = "unit_of_measure", nullable = false)
    private UnitOfMeasure unitOfMeasure;
    

=======
>>>>>>> 1af5ca64aeceb766f630c5431341dbd512787af3
    @Column(name = "description", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "sale_status", nullable = false)
    private SaleStatus saleStatus;

    
<<<<<<< HEAD
    @Column(name = "avg_rating", columnDefinition = "float default 0")

=======
    @Column(name = "avg_rating", nullable = true, columnDefinition = "float default 0")
>>>>>>> 1af5ca64aeceb766f630c5431341dbd512787af3
    private Float avgRating;

    //not yet building relationship
    @Column(name = "creator_id", nullable = false) 
    private int creatorId;

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;
    
<<<<<<< HEAD

    @Column(name = "number_of_sold", columnDefinition = "integer default 0")
    private Integer numberOfSold;
    
    //This service is bought by many orders which construct service in order = service order item 

=======
    //@Transient
    //@Formula("SELECT DISTINCT COUNT(soi.service_order_item_id) FROM service_order_item soi WHERE soi.service_id = service_id")
    @Column(name = "number_of_sold", columnDefinition = "integer default 0")
    private Integer numberOfSold;
    
>>>>>>> 1af5ca64aeceb766f630c5431341dbd512787af3
    @OneToMany(mappedBy = "service")
    @JsonIgnore
    private List<ServiceOrderItem> serviceOrderItemList;
    
<<<<<<< HEAD
    @OneToMany(mappedBy = "service")
    @JsonIgnore
    private List<PackageServiceItem> packageServiceItemList;
=======
    
>>>>>>> 1af5ca64aeceb766f630c5431341dbd512787af3


}


