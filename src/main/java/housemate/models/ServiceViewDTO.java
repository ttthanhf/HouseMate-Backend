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
	
	@JsonInclude(value = Include.NON_EMPTY)
	List<PackagePrice> priceList;

	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PackagePrice{ //let this class to access directly
		private int duration_value;
		private UsageDurationUnit duration_unit;
		private int originalPrice;
		private int salePrice;
		
		public PackagePrice setPackagePrice(Service service, int duration_value, UsageDurationUnit duration_unit ) {
			int originalPrice = service.getOriginalPrice() * duration_value;
			int salePrice = service.getSalePrice() * duration_value;
			return new PackagePrice(duration_value, duration_unit,originalPrice,salePrice);
		}
		
		
	}
	
}





