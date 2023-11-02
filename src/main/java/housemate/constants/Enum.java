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
		NAME("titleName"),
		PRICE("finalPrice"),
		NUMBER_OF_SOLD("numberOfSold");

		private String fieldName;
	}
	
	public enum ServiceConfiguration {
		SERVICE_GROUPS,
		SERVICE_UNITS;
	}
	
	public enum StaffWorkingStatus {
		ACTIVE,
		INACTIVE,
		BANNED;
	}
	
	public enum TaskStatus {
		PENDING_APPLICATION, //waiting for staff apply apply for job
		PENDING_WORKING,//found staff - waiting for staff coming
		INCOMING,//staff coming in around 4 5 6 12 hour
		CANCELLED_CAUSE_NOT_FOUND_STAFF,
		CANCELLED_BY_STAFF,
		CANCELLED_BY_CUSTOMER,
		ARRIVED,//staff arrived - start doing
		DOING,
		DONE;//staff finish task
	}
	
	public enum TaskReportType {
		ARRIVED,
		DOING,
		DONE;
	}
}
