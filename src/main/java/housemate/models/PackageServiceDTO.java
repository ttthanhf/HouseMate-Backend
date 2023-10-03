package housemate.models;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.annotation.ReadOnlyProperty;

import housemate.constants.Enum.IdType;
import housemate.constants.Enum.SaleStatus;
import housemate.constants.Enum.UsageDurationUnit;
import housemate.constants.validations.ExistingId;
import housemate.constants.validations.UniqueTitleName;
import housemate.entities.PackageServiceItem;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class PackageServiceDTO {

	@UniqueTitleName
	@NotBlank
	private String titleName;

	@NotNull
	@Positive
	@ReadOnlyProperty
	@Hidden
	private int originalPrice = 0; // default value = 0

	@NotNull
	@Positive
	private int salePrice;

	@NotBlank
	private String description;

	
	private SaleStatus saleStatus = SaleStatus.NOT_AVAILABLE;

	@NotNull
	@Hidden
	private int creatorId = 1; // auto get the current admin account will be fix in the feature

	@NotEmpty
	@Size(min = 2, message = "Choose at least 2 child services into package")
	@Schema(description = "If the childServices Exist - Not allow to delete or change the existing child Service Id")
	List<ChildService> childServices;

	public List<PackageServiceItem> getPackageServiceItem(List<ChildService> childServices) {
		List<PackageServiceItem> packageItemList = childServices.stream()
				.map(s -> PackageServiceItem.builder().serviceId(s.getServiceId()).usageLimit(s.getUsageLimit())
						.usageDurationValue(s.getUsageDurationValue()).usageDurationUnit(s.getUsageDurationUnit())
						.build())
				.collect(Collectors.toList());
		return packageItemList;

	}
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class ChildService {

	@NotNull
	@ExistingId(type = IdType.SERVICE)
	// check if service id exists previously in service table
	private int serviceId;

	@NotNull
	@Schema(example = "20")
	private int usageLimit;

	@NotNull
	@Schema(example = "1")
	private int usageDurationValue;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Schema(example = "MONTH")
	private UsageDurationUnit usageDurationUnit;
}
