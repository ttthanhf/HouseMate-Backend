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

	public static enum SaleStatus {
		AVAILABLE,
		ONSALE,
		DISCONTINUED
	}

	@Getter
	@AllArgsConstructor
	public static enum ServiceField {
		NAME("titleName"),
		PRICE("finalPrice"),
		NUMBER_OF_SOLD("numberOfSold");

	    	private String fieldName;
	}
	
	public enum TaskStatus {
		PENDING_APPLICATION,//waiting for staff apply apply for job
		PENDING_WORKING,//found staff - waiting for staff coming
		INCOMING,//task status of next coming task
		CANCELLED_CAUSE_NOT_FOUND_STAFF,//task be cancelled caused not found any staff
		CANCELLED_BY_STAFF,//task cancelled by staff
		CANCELLED_BY_CUSTOMER,//task cancelled by customer
		ARRIVED,//staff arrived - start doing
		DOING,//staff in the status of doing housework
		DONE,//staff finish task
		CANCELLED;
	}
	
	public enum TaskReportType {
		ARRIVED,
		DOING,
		DONE;
	}
	
	public enum TaskMessType {
		OK,
		REJECT_UPDATE_TASK,
		REJECT_CANCELLED,
		REJECT_APPROVE_STAFF,
		REJECT_REPORT_TASK
	}
	
}
