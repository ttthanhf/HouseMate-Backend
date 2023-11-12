package housemate.constants;

import java.util.EnumSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import housemate.services.ServiceConfigService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;

@Getter
public enum ServiceConfiguration {
    SERVICE_GROUPS(true), SERVICE_UNITS(true),
    OFFICE_HOURS_START(false), OFFICE_HOURS_END(false),
    FIND_STAFF_HOURS(false), MINIMUM_RETURN_HOURS(false),
    DURATION_HOURS_CUSTOMER_SHOULD_NOT_CANCEL_TASK(false),
    DURATION_HOURS_STAFF_SHOULD_NOT_CANCEL_TASK(false),
    DURATION_HOURS_ALLOW_BAD_STAFF_PROFICENT_SCORE_APPLY(false),
    DURATION_MINUTES_TIMES_STAFF_START_REPORT(false),
    DURATION_HOURS_SEND_INCOMING_NOTI_BEFORE(false),
    DURATION_HOURS_SYST_AUTO_DONE_TASK(false),
    MINUS_POINTS_FOR_CUSTOMER_CANCEL_TASK(false),
    MINUS_POINTS_FOR_STAFF_CANCEL_TASK(false),
    MINUS_POINTS_FOR_NOT_COMPLETE_REPORT_TASK(false),
    PLUS_SCORE_PER_SUCCESS_TASK(false),
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