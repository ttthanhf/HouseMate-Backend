package housemate.models;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import housemate.constants.Enum.UsageDurationUnit;
import housemate.entities.PackageServiceItem;
import housemate.entities.Service;
import housemate.entities.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceViewDTO {

	private Service service;

	@JsonInclude(value = Include.NON_EMPTY)
	private List<ServiceType> typeList;

	@JsonInclude(value = Include.NON_EMPTY)
	private List<PackageServiceItem> packageServiceItemList;

	List<ServicePrice> priceList;

	List<String> images; // Update later

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ServicePrice { // let this class to access directly
		private int durationValue;
		private UsageDurationUnit durationUnit;
		private int originalPrice;
		private int salePrice;
		@JsonProperty(value = "extensionFeePerMonth")
		private int extensionFee;

		public ServicePrice setPriceForComboMonth(
							Service service,
							int duration_value,
							UsageDurationUnit duration_unit,
							int extensionFee
							){
			int originalPrice = service.getOriginalPrice() + extensionFee * duration_value;
			int salePrice = (originalPrice - service.getSalePrice()) * extensionFee * duration_value;
			return new ServicePrice(duration_value, duration_unit, originalPrice, salePrice, extensionFee);
		}
	}

}
