package housemate.models;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
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
	
	@Schema(description = "Ađ Link Images Of Service")
	private String imageUrl;
	
	@Hidden
	@Schema(description = "Is package: true ? false")
	private boolean isPackage;

	Set<String> typeNameList ;
	
	Map<Integer, Integer> serviceChildList; //one for id - one for qu


	
	
	
}
