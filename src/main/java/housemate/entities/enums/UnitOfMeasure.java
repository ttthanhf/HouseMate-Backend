package housemate.entities.enums;

public enum UnitOfMeasure {

	HOUR("Hour"),
	KG("Kg"),
	TIME("Time");
	
	private String unitOfMeasure;
	
	UnitOfMeasure(String unitOfMeasure) {
		this.unitOfMeasure = unitOfMeasure;
	}
}
