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

/**
 *
 * @author ThanhF
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "package_service_item")
public class PackageServiceItem {

	
	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "package_service_id", insertable = false, updatable = false)
    private PackageService packageService;

	@JsonBackReference
    @ManyToOne
    @JoinColumn(name = "service_id", insertable = false, updatable = false)
    private Service service;

	@Id
	@JsonIgnore
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

