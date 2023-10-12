package housemate.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/*This class contain all kind of Housemate Enum*/
public class Enum {

	public static enum ServiceCategory {
		SINGLES,
		PACKAGES,
		GENERAL
		
	}

	public static enum UsageDurationUnit {
		DAY,
		WEEK,
		MONTH
		
	}

	public static enum SortRequired {
		ASC,
		DESC
	}

	public static enum GroupType {
		CLEANING_SERVICE,
		RETURN_SERVICE,
		DELIVERY_SERVICE,
		OTHER
	}

	public static enum SaleStatus {
		AVAILABLE,
		ONSALE,
		DISCONTINUED
	}

	public static enum UnitOfMeasure {
		HOUR,
		KG,
		TIME,
		COMBO
	}
	
	@Getter
	@AllArgsConstructor
	public static enum ServiceField {
		NAME("titleName"),
		PRICE("originalPrice"),
		NUMBER_OF_SOLD("numberOfSold")
		;

		private String fieldName;
	}

}
