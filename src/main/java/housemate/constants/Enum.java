package housemate.constants;

/*This class contain all kind of Housemate Enum*/
public class Enum {

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
		NOT_AVAILABLE("not available for sale"),
		AVAILABLE("available for sale"),
		DISCONTINUED("discontinued");

		private String saleStatus;

		SaleStatus(String saleStatus) {
			this.saleStatus = saleStatus;
		}

		public String getSaleStatus() {
			return saleStatus;
		}
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

	public static enum PackageServiceField {

		NAME("titleName"),
		PRICE("salePrice"),
		NUMBER_OF_SOLD("numberOfSold");

		private String fieldName;

		PackageServiceField(String fieldName) {
			this.fieldName = fieldName;
		}

		public String getFieldName() {
			return fieldName;
		}
	}

}
