package housemate.entities.enums;

public enum SaleStatus {
	
	NOT_AVAILABLE("not available for sale"),
	AVAILABLE("available for sale"),
	DISCONTINUED("discontinued");
	
	private String saleStatus;
	
	SaleStatus(String saleStatus) {
		this.saleStatus = saleStatus;
	}
	
}
