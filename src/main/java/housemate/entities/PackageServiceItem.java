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
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import housemate.constants.Enum.UsageDurationUnit;
import housemate.entities.PackageService;
/**
 *
 * @author Anh
 */


@Data
@AllArgsConstructor
@NoArgsConstructor
 class RelationShipId implements Serializable {
    private int packageId;
    private int serviceId;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "package_service_item")
@Entity
@IdClass(RelationShipId.class)
public class PackageServiceItem {

	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "package_service_id")
    private PackageService packageService;

	@JsonBackReference
    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;

	@Id
	@Column(name = "package_service_id")
    private int packageId;
	
	@Id
	@Column(name = "service_id")
    private int serviceId;
	
    @Column(name = "quantity")
    private int usageLimit;
    
    @Column(name = "duration_value")
    private int usageDurationValue;

    @Column(name = "duration_unit")
    @Enumerated(EnumType.STRING)
    private UsageDurationUnit usageDurationUnit;


}

