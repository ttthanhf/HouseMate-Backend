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

	@JsonInclude(value = Include.NON_EMPTY)
	private List<ServiceType> typeList;

	@JsonInclude(value = Include.NON_EMPTY)
	private List<PackageServiceItem> packageServiceItemList;

	List<ServicePrice> priceList;

	//TODO: Update imgList later 
	List<String> images; 

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ServicePrice { // let this class to access directly
		private int durationValue;
		private UsageDurationUnit durationUnit;
		private int originalPrice;
		private int salePrice;

		public ServicePrice setPriceForComboMonth(Service service, int duration_value, UsageDurationUnit duration_unit,
				int percentAddedValue) {
			int originalPrice = service.getOriginalPrice() * percentAddedValue/100 ;
			int salePrice =  service.getSalePrice() * percentAddedValue/100;
			if (salePrice < 0 || service.getSalePrice() == 0) salePrice = 0; //No sale for this service
			return new ServicePrice(duration_value, duration_unit, originalPrice, salePrice);
		}
	}

}
