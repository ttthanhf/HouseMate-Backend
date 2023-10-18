package housemate.models;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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

	@JsonInclude(value = Include.NON_NULL)
	private List<ServiceType> typeList;

	@JsonInclude(value = Include.NON_NULL)
	private List<PackageServiceItem> packageServiceItemList;

	List<ServicePrice> priceList;

	// TODO: Update imgList later
	List<String> images;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ServicePrice { // let this class to access directly
		private float durationValue;
		private UsageDurationUnit durationUnit;
		private float originalPrice;
		private float final_price;

		public ServicePrice setPriceForComboMonth(Service service, int duration_value, UsageDurationUnit duration_unit, float percentAddedValue) {
			float originalPrice = service.getOriginalPrice() * percentAddedValue;
			float final_price = service.getFinalPrice() * percentAddedValue;
			if (final_price < 0 || service.getSalePrice() == 100)
				final_price = 0; // No sale for this service

			return new ServicePrice(duration_value, duration_unit, originalPrice, (int) final_price);
		}
	}

}
