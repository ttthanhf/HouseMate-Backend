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
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 *
 * @author Anh
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
class IdComboPackageServiceItem implements Serializable {
	private int packageServiceId; // same name with the key
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

	@Id
	@Column(name = "package_service_id")
	private int packageServiceId;

	@Id
	@Column(name = "service_id")
	private int singleServiceId;

	@JsonInclude(value = Include.NON_NULL)
	@Transient
	private String singleServiceName;

	@Column(name = "quantity")
	private int quantity;

	@ManyToOne
	@JoinColumn(name="service_id", referencedColumnName = "service_id", updatable = false, insertable = false)
	private Service service;
	
	@Transient
	private List<ServiceType> typeList;
}
