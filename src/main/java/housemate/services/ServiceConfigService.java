package housemate.services;

import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import housemate.constants.Role;
import housemate.constants.ServiceConfiguration;
import housemate.entities.ServiceConfig;
import housemate.models.ServiceConfigNewDTO;
import housemate.repositories.ServiceConfigRepository;
import housemate.utils.AuthorizationUtil;
import housemate.utils.StringUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import net.minidev.json.JSONObject;

@Service
public class ServiceConfigService {

	@Autowired
	ServiceConfigRepository servConfRepo;

	@Autowired
	private AuthorizationUtil authorizationUtil;

	ModelMapper mapper = new ModelMapper();

	public ResponseEntity<?> getAllByServiceConfigType(ServiceConfiguration serviceConfiguration) {
		List<ServiceConfig> configValues = servConfRepo.findAllByConfigType(serviceConfiguration);
		return ResponseEntity.ok(configValues);
	}

	public ResponseEntity<?> getAll() {
		JSONObject servConfCollection = new JSONObject();
		for (ServiceConfiguration servConfigType : ServiceConfiguration.values())
			servConfCollection.put(servConfigType.name(), servConfRepo.findAllByConfigType(servConfigType));
		return ResponseEntity.ok(servConfCollection);
	}

	@Transactional
	public ResponseEntity<?> createNewServConf(HttpServletRequest request, ServiceConfigNewDTO newServiceConf) {

		if (!authorizationUtil.getRoleFromAuthorizationHeader(request).equals(Role.ADMIN.toString()))
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");

		ServiceConfig newServConf = mapper.map(newServiceConf, ServiceConfig.class);
		newServConf.setConfigValue(StringUtil.formatedString(newServiceConf.getConfigValue()));
		ServiceConfig savedServConf = servConfRepo
				.findByConfigTypeAndConfigValue(newServConf.getConfigType().name(), newServConf.getConfigValue())
				.orElse(null);

		if (savedServConf != null)
			return ResponseEntity.ok().body(savedServConf);

		try {
			savedServConf = servConfRepo.save(newServConf);
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return ResponseEntity.badRequest().body("Saved failed");
		}
		return ResponseEntity.ok(savedServConf);
	}

	@Transactional
	public ResponseEntity<?> updateServConf(HttpServletRequest request, int serviceConfigId,
			ServiceConfigNewDTO newServiceConf) {

		ServiceConfig existedServConf = servConfRepo.findById(serviceConfigId).orElse(null);

		if (existedServConf == null)
			return ResponseEntity.badRequest().body("Not found to update !. Let create new one");

		newServiceConf.setConfigValue(StringUtil.formatedString(newServiceConf.getConfigValue()));

		if (!newServiceConf.getConfigType().equals(existedServConf.getConfigType())
				|| !newServiceConf.getConfigValue().equals(existedServConf.getConfigValue())) {
			ServiceConfig foundedDuplicatedConf = servConfRepo.findByConfigTypeAndConfigValue(
					newServiceConf.getConfigType().name(), newServiceConf.getConfigValue()).orElse(null);
			if (foundedDuplicatedConf != null)
				return ResponseEntity.badRequest().body("This config has been duplicated !");

			existedServConf.setConfigType(newServiceConf.getConfigType());
			existedServConf.setConfigValue(newServiceConf.getConfigValue());

		}
		return ResponseEntity.ok(existedServConf);
	}

	@Transactional
	public ResponseEntity<?> deleteConfigValue(HttpServletRequest request, int serviceConfigId) {
		ServiceConfig existedServConf = servConfRepo.findById(serviceConfigId).orElse(null);
		if (existedServConf == null)
			return ResponseEntity.badRequest().body("Not found to delete !");
		servConfRepo.delete(existedServConf);
		return ResponseEntity.ok("Deleted Successfully");
	}

}
