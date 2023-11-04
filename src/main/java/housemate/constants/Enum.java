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
	
	public enum ServiceConfiguration {
		SERVICE_GROUPS,
		SERVICE_UNITS,
		OFFICE_HOURS_START,
		OFFICE_HOURS_END,
		FIND_STAFF_HOURS,
		MINIMUM_RETURN_HOURS,
		DURATION_TIMES_CUSTOMER_SHOULD_NOT_CANCEL_TASK,
		DURATION_TIMES_STAFF_SHOULD_NOT_CANCEL_TASK,
		DURATION_TIMES_ALLOW_BAD_STAFF_PROFICENT_SCORE_APPLY,
		MINUS_POINTS_FOR_CUSTOMER_CANCEL_TASK,
		MINUS_POINTS_FOR_STAFF_CANCEL_TASK,
		BAD_STAFF_PROFICIENT_SCORE,
		DURATION_TIMES_STAFF_START_REPORT
	}
	
	public enum AccountStatus {
		ACTIVE,
		INACTIVE,
		BANNED;
	}
	
	public enum TaskStatus {
		PENDING_APPLICATION,//waiting for staff apply apply for job
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
