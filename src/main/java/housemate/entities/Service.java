/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.entities;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import housemate.constants.Enum.SaleStatus;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Anh
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
	@Column(name = "service_id")
	private int serviceId;

	@Column(name = "title_name", unique = true, nullable = false)
	private String titleName;

	@Column(name = "original_price", nullable = false)
	private int originalPrice;

	@Column(name = "final_price", nullable = false)
	private int finalPrice;

	@Column(name = "unit_of_measure", nullable = false)
	private String unitOfMeasure;

	@Column(name = "description", nullable = false)
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(name = "sale_status", nullable = false)
	private SaleStatus saleStatus;

	@Column(name = "group_type", nullable = false)
	private String groupType;

	@Column(name = "avg_rating", columnDefinition = "float default 0")
	private float avgRating;

	@Column(name = "number_of_sold", columnDefinition = "integer default 0")
	private int numberOfSold;

	@Column(name = "isPackage", nullable = false)
	private boolean isPackage;
	
	@Column(name = "min_usage")
	private int min;
	
	@Column(name = "max_usage")
	private int max;

	@Transient
	private List<Image> images = List.of();
	
	@JsonInclude(value = Include.NON_NULL)
	@Transient
	private Integer numberOfReview;

	@JsonInclude(value = Include.NON_NULL)
	@Transient
	private Integer numberOfComment;

}
