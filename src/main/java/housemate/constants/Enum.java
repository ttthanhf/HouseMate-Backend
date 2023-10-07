package housemate.constants;

/*This class contain all kind of Housemate Enum*/
public class Enum {

	public static enum ServiceCategory {
		singles,
		packages,
		general;
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
		NOT_AVAILABLE;
	}

	public static enum UnitOfMeasure {

		HOUR("Hour"),
		KG("Kg"),
		TIME("Time");

		private String unitOfMeasure;

		UnitOfMeasure(String unitOfMeasure) {
			this.unitOfMeasure = unitOfMeasure;
		}

		public String getUnitOfMeasure() {
			return unitOfMeasure;
		}
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
