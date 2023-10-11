package housemate.models;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import housemate.constants.Enum.SaleStatus;
import housemate.constants.Enum.UnitOfMeasure;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceNewDTO implements Serializable{

	private static final long serialVersionUID = 1L;

	@Schema(description = "Title name" )
	private String titleName;

	@Positive(message = "Price must be greater than 0")
	@Schema(description = "Original price")
	private int originalPrice;
	
	@PositiveOrZero(message = "Salep Price must be greater than 0")
	@Schema(description = "Sale price")
	private int salePrice;

	@Schema(description = "The Unit of measure in one of these type: KG, HOUR, TIME, COMBO. With package - unit measure default = COMBO")
	private UnitOfMeasure unitOfMeasure;

	@NotBlank(message = "Filling the description of the service")
	@Schema(description = "Description of your service")
	private String description;

	
	@Schema(description = "Default: AVAILABLE If salePrice Exist -> ONSALE")
	private SaleStatus saleStatus;
	
	@Schema(description = "Images Of Service")
	private String image;
	
	@Hidden
	@Schema(description = "Is package: true ? false")
	private boolean isPackage;

	@Schema(example = "[\r\n"
			+ "\"type 1\",\r\n"
			+ "\"type 2\"\r\n"
			+ "  ]" ,description = "how many types this service has")
	@JsonInclude(value = Include.NON_NULL)
	Set<String> typeNameList ;
	
	@Schema(example = "{\r\n"
			+ "\"1\": 0,\r\n"
			+ "\"2\": 0,\r\n"
			+ "\"3\": 0\r\n"
			+ "  }", description = "Choose single services from single service list and set the quantity")
	@JsonInclude(value = Include.NON_NULL)
	Map<Integer, Integer> serviceChildList; //one for id - one for quantity


	
	
	
}
