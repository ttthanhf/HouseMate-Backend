package housemate.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/*This class contain all kind of Housemate Enum*/
public class Enum {

	public static enum ServiceCategory {
		SINGLE,
		PACKAGE
	}

	public static enum TimeUnit {
		MONTH,
		DAY,
		WEEK
	}

	public static enum SortRequired {
		ASC,
		DESC
	}

	public static enum GroupType {
		HOURLY_SERVICE,
		RETURN_SERVICE,
		DELIVERY_SERVICE,
		OTHER
	}

	public static enum SaleStatus {
		AVAILABLE,
		ONSALE,
		DISCONTINUED
	}


	@Getter
	@AllArgsConstructor
	public static enum ServiceField {
		NAME("titleName"), PRICE("finalPrice"),
		NUMBER_OF_SOLD("numberOfSold");

		private String fieldName;
	}
	
}
