package housemate.constants;

import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import housemate.entities.ServiceConfig;
import housemate.repositories.ServiceConfigRepository;
import java.util.List;

public enum ServiceConfiguration {
	 SERVICE_GROUPS,
	 SERVICE_UNITS;
	
	//each enum will receive the Set Collection As Parameter
	private static final Set<String> collection;
	
	public void addValue(ServiceConfig value) {
		collection.add(value.getConfigValue());
	}
	
	public void addAllFromList(List<ServiceConfig> servConflist) {
		for (ServiceConfig serviceConfig : servConflist) {
			this.addValue(serviceConfig);
		}
	}
	
	public void resetAndAddAll(List<ServiceConfig> servConflist) {
		collection.clear();
		addAllFromList(servConflist);
	}

	public Set<String> getCollectionSet() {
		return collection;
	}	
	
	@Autowired
	static ServiceConfigRepository servConfRepo;
	
	static {
		collection = new HashSet<>();
		for(ServiceConfiguration servConfigType : ServiceConfiguration.values()) {
			List<ServiceConfig> servConfList = servConfRepo.findAllByConfigType(servConfigType);
			for (ServiceConfig serviceConfig : servConfList) {
				ServiceConfiguration.SERVICE_GROUPS.addValue(serviceConfig);
			}
		}
	}

	@Override
	public String toString() {
		String enumNotitification = "[ ";
		for(ServiceConfiguration servConfigType : ServiceConfiguration.values()) {
			enumNotitification += servConfigType + " ";
		}
		return enumNotitification + " ]";
	}
	
	
		
}
