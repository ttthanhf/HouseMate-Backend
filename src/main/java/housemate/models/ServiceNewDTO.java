package housemate.models;

import java.util.Map;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import housemate.constants.Enum.SaleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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

	@Schema(description = "Sale price")
	@PositiveOrZero(message = "Set the Final Price from 0 to upper and smaller than or equal Original Price")
	private Integer finalPrice;

	@NotNull(message = "The unit of measure type must not be empty")
	@Schema(description = "The Unit of measure in one of these type: KG, HOUR, TIME, COMBO."
			+ " With package - unit measure default = COMBO")
	private String unitOfMeasure;

	@NotBlank(message = "Filling the description of the service")
	@Schema(description = "Description of your service")
	private String description;

	@Schema(description = "Default: AVAILABLE If salePrice Exist -> ONSALE")
	@JsonInclude(value = Include.NON_NULL)
	private SaleStatus saleStatus;

	@NotNull(message = "The group type must not be null")
	@Schema(description = "The Group Type in one of these type: "
			+ "CLEANING_SERVICE, RETURN_SERVICE, DELIVERY_SERVICE")
	private String groupType;

	@NotNull(message = "Specify this category of service isPackage or single by true or false")
	@Schema(description = "Is package: true ? false")
	private Boolean isPackage;

	@Schema(example = "[\r\n" + "\"type 1\",\r\n" + "\"type 2\"\r\n"
			+ "]", description = "how many types this service has")
	@JsonInclude(value = Include.NON_NULL)
	private Set<String> typeNameList;

	@Schema(example = "{\r\n" + "\"1\": 0,\r\n" + "\"2\": 0,\r\n" + "\"3\": 0\r\n"
			+ "}", description = "Choose single services from single service list and set the quantity")
	@JsonInclude(value = Include.NON_NULL)
	private Map<Integer, Integer> serviceChildList; // one for id - one for quantity

	@NotEmpty(message = "You have to must set the price cycle list for this service")
	@Schema(example = "{\r\n" + "\"3\": 1000,\r\n" + "\"6\": 1000,\r\n" + "\"9\": 1000\r\n," + "\"12\": 1000\r\n"
			+ "}", description = "Set the price for each cycle")
	@Size(min = 4, max = 4, message = "Have to set price foreach 4 cycles : 3, 6, 9 ,12 of this service")
	private Map<Integer, Integer> periodPriceServiceList;

	@PositiveOrZero
	private int min;

	@PositiveOrZero
	private int max;

}
