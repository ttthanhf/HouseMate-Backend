package housemate.services;


import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import housemate.constants.ServiceConfiguration;
import housemate.entities.ServiceConfig;
import housemate.models.ServiceConfigNew;
import housemate.repositories.ServiceConfigRepository;
import housemate.utils.StringUtil;
import jakarta.transaction.Transactional;
import net.minidev.json.JSONObject;

@Service
public class ServiceConfigService {

	@Autowired
	ServiceConfigRepository servConfRepo;

	ModelMapper mapper = new ModelMapper();

	public ResponseEntity<?> getAllByServiceConfigType(ServiceConfiguration serviceConfiguration){
		List<ServiceConfig> configValues = servConfRepo.findAllByConfigType(serviceConfiguration);
		return ResponseEntity.ok(configValues);
	}
	
	public ResponseEntity<?> getAll(){
        JSONObject servConfCollection = new JSONObject();
		for(ServiceConfiguration servConfigType : ServiceConfiguration.values()) {
			servConfCollection.put(servConfigType.name(), servConfRepo.findAllByConfigType(servConfigType));
		}
		return ResponseEntity.ok(servConfCollection);
	}
	
	@Transactional
	public ResponseEntity<?> createNewServConf(ServiceConfigNew newServiceConf) {

		ServiceConfig newServConf = mapper.map(newServiceConf, ServiceConfig.class);
		newServConf.setConfigValue(StringUtil.formatedString(newServiceConf.getConfigValue()));
		ServiceConfig savedServConf = servConfRepo
				.findByConfigTypeAndConfigValue(newServConf.getConfigType(), newServConf.getConfigValue())
				.orElse(null);

		if (savedServConf != null) 
			return ResponseEntity.ok().body("You have configed before" + savedServConf);

		try {
			savedServConf = servConfRepo.save(newServConf);
			for(ServiceConfiguration servConfigType : ServiceConfiguration.values()) {
				if(savedServConf.getConfigType().equals(servConfigType.name())) {
					servConfigType.addValue(savedServConf);
					break;
				}
			}
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return ResponseEntity.badRequest().body("Saved failed");
		}

		return ResponseEntity.ok(savedServConf);
	}
	
	@Transactional
	public ResponseEntity<?> updateServConf(int serviceConfigId, ServiceConfigNew newServiceConf) {

		ServiceConfig existedServConf = servConfRepo.findById(serviceConfigId).orElse(null);

		if (existedServConf == null)
			return ResponseEntity.badRequest().body("Not found to update !");

		newServiceConf.setConfigValue(StringUtil.formatedString(newServiceConf.getConfigValue()));
		
		if (servConfRepo.findByConfigTypeAndConfigValue(newServiceConf.getConfigType(),
				newServiceConf.getConfigValue()) != null) {
			return ResponseEntity.badRequest().body("This config has been duplicated !");
		}
		existedServConf.setConfigType(newServiceConf.getConfigType());
		existedServConf.setConfigValue(newServiceConf.getConfigValue());

		for(ServiceConfiguration servConfigType : ServiceConfiguration.values()) {
			servConfigType.resetAndAddAll(servConfRepo.findAllByConfigType(servConfigType));
		}

		return ResponseEntity.ok(existedServConf);
	}
	
	@Transactional
	public ResponseEntity<?> deleteConfigValue(int serviceConfigId) {
		ServiceConfig existedServConf = servConfRepo.findById(serviceConfigId).orElse(null);
		if (existedServConf == null)
			return ResponseEntity.badRequest().body("Not found to delete !");
		servConfRepo.delete(existedServConf);
		return ResponseEntity.ok(existedServConf);
	}
	
	

}
