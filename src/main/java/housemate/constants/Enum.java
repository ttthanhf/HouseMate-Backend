package housemate.constants;

/*This class contain all kind of Housemate Enum*/
public class Enum {

	public static enum ServiceCategory {
		SINGLES,
		PACKAGES,
		GENERAL;
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
	
	public static enum IdType{
		SERVICE,
		PACKAGE;
	}

	public static enum SaleStatus {
		AVAILABLE,
		ONSALE,
		DISCONTINUED,
	}

	public static enum UnitOfMeasure {
		HOUR,
		KG,
		TIME,
		COMBO;
	}

	public static enum ServiceField {
		NAME("titleName"),
		PRICE("price"),
		NUMBER_OF_SOLD("numberOfSold");

		private String fieldName;

		ServiceField(String fieldName) {
			this.fieldName = fieldName;
		}

		public String getFieldName() {
			return fieldName;
		}
	}

}
