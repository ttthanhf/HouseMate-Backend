package housemate.models;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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
		private int periodId;
		private float periodValue;
		private String periodName;
		private float originalPrice;
		private float finalPrice;

		public ServicePrice setPriceForComboMonth(Service service, int periodId, int periodValue, String periodName, float percentAddedValue) {
			float originalPrice = service.getOriginalPrice() * percentAddedValue;
			float finalPrice = service.getFinalPrice() * percentAddedValue;
			if (finalPrice < 0 || service.getSalePrice() == 100)
				finalPrice = 0; // No sale for this service

			return new ServicePrice(periodId, periodValue, periodName, originalPrice, (int) Math.ceil((double) finalPrice));
		}
	}

}
