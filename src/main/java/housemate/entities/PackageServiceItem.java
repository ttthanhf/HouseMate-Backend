/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author Anh
 */


@Data
@AllArgsConstructor
@NoArgsConstructor
 class IdComboPackageServiceItem implements Serializable {
    private int packageServiceId; //same name with the key
    private int singleServiceId;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "package_service_item")
@Entity
@IdClass(IdComboPackageServiceItem.class)
public class PackageServiceItem {

    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;

	@Id
	@Column(name = "package_service_id")
	@JsonIgnore
    private int packageServiceId;
	
	@Id
	@Column(name = "service_id")
	@JsonIgnore
    private int singleServiceId;
	
    @Column(name = "quantity")
    private int quantity;
    


}

