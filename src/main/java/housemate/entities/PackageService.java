/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.entities;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.rest.core.config.Projection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import housemate.constants.Enum.SaleStatus;
import jakarta.persistence.*;
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
@Table(name = "package_service")
public class PackageService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(value = AccessLevel.NONE)
    @Column(name = "package_service_id")
    private int packageServiceId;

    @Column(name = "title_name", unique = true, nullable = false)
    private String titleName;

    @Column(name = "original_price", nullable = false)
    private int originalPrice;
    
    @Column(name = "sale_price", nullable = false)
    private int salePrice;
    
    @Column(name = "description", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "sale_status", nullable = false)
    private SaleStatus saleStatus;
    
    @Column(name = "avg_rating", columnDefinition = "float default 0")
    private Float avgRating;

    //not yet build relationship
    @Column(name = "creator_id", nullable = false) 
    private int creatorId;

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Column(name = "number_of_sold", columnDefinition = "integer default 0")
    private Integer numberOfSold;
    
    
    @JsonManagedReference
    @OneToMany(mappedBy = "packageService")
    List<PackageServiceItem> packageServiceItemList;
   
    @OneToMany(mappedBy = "packageService")
    @JsonIgnore
    private List<ServiceOrderItem> serviceOrderItemList;
    
    @Projection(name = "summary", types = PackageService.class)
    public interface Summary {
    	int getPackageServiceId();
    	String getTitleName();
    	int getOriginalPrice();
    	int getSalePrice();
    	int getAvgRating();
    	int getNumberOfSold();
    }
    
    
}

