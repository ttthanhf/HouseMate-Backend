package housemate.models;

import java.util.List;
import java.util.Map;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import housemate.constants.Enum.GroupType;
import housemate.constants.Enum.SaleStatus;
import housemate.constants.Enum.UnitOfMeasure;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class ServiceNewDTO {

	@NotEmpty(message = "Title name must not be empty")
	@Size(min = 5, max = 255, message = "Title name must have length from 5 to 255")
	@Schema(description = "Title name")
	private String titleName;

	@Min(value = 1000, message = "Price must from 1,000 VND upper")
	@Schema(description = "Original price")
	private Integer originalPrice;

	@Min(value = 0, message = "The percent for sale price must from 0 - 100%")
	@Max(value = 100, message = "The percent for sale price must from 0 - 100%")
	@Schema(description = "Sale price")
	private Integer salePrice;

	@NotNull(message = "The unit of measure type must not be empty")
	@Schema(description = "The Unit of measure in one of these type: KG, HOUR, TIME, COMBO."
			+ " With package - unit measure default = COMBO")
	private UnitOfMeasure unitOfMeasure;

	@NotBlank(message = "Filling the description of the service")
	@Schema(description = "Description of your service")
	private String description;

	// TODO: FE not allow user set this sale status, system automatically sets bases
	// on the salePercent value exists or not
	@Schema(description = "Default: AVAILABLE If salePrice Exist -> ONSALE")
	@JsonInclude(value = Include.NON_NULL)
	private SaleStatus saleStatus;

	@NotEmpty(message = "Require at least one image")
	@Schema(description = "Images Of Service")
	private List<String> images;

	@NotNull(message = "The group type must not be null")
	@Schema(description = "The Group Type in one of these type: "
			+ "CLEANING_SERVICE, RETURN_SERVICE, DELIVERY_SERVICE, OTHER")
	private GroupType groupType;

	@NotNull(message = "Specify this category of service isPackage or single by true or false")
	@Schema(description = "Is package: true ? false")
	private Boolean isPackage;

	// TODO: FE constraint to pop up only for creating single service
	@Schema(example = "[\r\n" + "\"type 1\",\r\n" + "\"type 2\"\r\n"
			+ "]", description = "how many types this service has")
	@JsonInclude(value = Include.NON_NULL)
	Set<String> typeNameList;

	// TODO: FE constraint to pop up only for creating single service
	@Schema(example = "{\r\n" + "\"1\": 0,\r\n" + "\"2\": 0,\r\n" + "\"3\": 0\r\n"
			+ "}", description = "Choose single services from single service list and set the quantity")
	@JsonInclude(value = Include.NON_NULL)
	Map<Integer, Integer> serviceChildList; // one for id - one for quantity

}
