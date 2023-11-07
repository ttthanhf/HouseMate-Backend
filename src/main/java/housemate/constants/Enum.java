package housemate.constants;


import java.util.EnumSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import housemate.repositories.ServiceConfigRepository;
import housemate.services.ServiceConfigService;
import jakarta.annotation.PostConstruct;
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
	

	public enum AccountStatus {
		ACTIVE,
		INACTIVE,
		BANNED;
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
	
	@Getter
	public enum ServiceConfiguration {
	    SERVICE_GROUPS(true), SERVICE_UNITS(true),
	    OFFICE_HOURS_START(false), OFFICE_HOURS_END(false),
	    FIND_STAFF_HOURS(false), MINIMUM_RETURN_HOURS(false),
	    DURATION_HOURS_CUSTOMER_SHOULD_NOT_CANCEL_TASK(false),
	    DURATION_HOURS_STAFF_SHOULD_NOT_CANCEL_TASK(false),
	    DURATION_HOURS_ALLOW_BAD_STAFF_PROFICENT_SCORE_APPLY(false),
	    DURATION_MINUTES_TIMES_STAFF_START_REPORT(false),
	    MINUS_POINTS_FOR_CUSTOMER_CANCEL_TASK(false),
	    MINUS_POINTS_FOR_STAFF_CANCEL_TASK(false),
	    BAD_STAFF_PROFICIENT_SCORE(false);

	    private ServiceConfigService configService;
	    private boolean isMultiValue;

	    private ServiceConfiguration(boolean isMultiValue) {
		this.isMultiValue = isMultiValue;
	    }
	    public void setServiceConfig(ServiceConfigService serviceConfig) {
		this.configService = serviceConfig;
	    }
	    
	    @Component
	    public static class ServiceConfigInjector {
		@Autowired
		ServiceConfigService serviceConfig;

		@PostConstruct
		public void postConstruct() {
		    for (ServiceConfiguration config : EnumSet.allOf(ServiceConfiguration.class)) {
			config.setServiceConfig(this.serviceConfig);
		    }
		}
	    }
	    
	    public Integer getNum() {
		String configValue = null;
		try {
		    configValue = configService.getConfigValuesOfConfigTypeName(this.valueOf(this.name())).stream().findFirst()
				.orElseThrow(IllegalArgumentException::new);
		}catch (Exception e) {
		    e.printStackTrace();
		}
		return configValue == null ? null : Integer.parseInt(configValue);
	    }
	}
	
}
