package housemate.services;

import java.util.List;
import java.util.stream.Collectors;
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
	private ServiceConfigRepository servConfRepo;

	@Autowired
	private AuthorizationUtil authorizationUtil;
	
	private ModelMapper mapper = new ModelMapper();

	public ResponseEntity<?> getAllByServiceConfigType(ServiceConfiguration serviceConfiguration) {
	    List<ServiceConfig> serviceConfigList = servConfRepo.findAllByConfigType(serviceConfiguration);
		return ResponseEntity.ok(serviceConfigList);
	}
	 
	public List<String> getConfigValuesOfConfigTypeName(ServiceConfiguration serviceConfiguration) {
	    List<ServiceConfig> serviceConfigList = servConfRepo.findAllByConfigType(serviceConfiguration);
	    if (serviceConfigList == null)
		return List.of();
	    List<String> configValues = serviceConfigList.stream().map(x -> x.getConfigValue())
		    .collect(Collectors.toList());
	    return configValues;
	}
	
	public ResponseEntity<?> getAll() {
		JSONObject servConfCollection = new JSONObject();
		for (ServiceConfiguration servConfigType : ServiceConfiguration.values())
			servConfCollection.put(servConfigType.name(), servConfRepo.findAllByConfigType(servConfigType));
		return ResponseEntity.ok(servConfCollection);
	}

	@Transactional
	public ResponseEntity<?> createNewServConfig(HttpServletRequest request, ServiceConfigNewDTO newServiceConf) {

		if (!authorizationUtil.getRoleFromAuthorizationHeader(request).equals(Role.ADMIN.toString()))
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Từ chối truy cập");

		ServiceConfig newServConf = mapper.map(newServiceConf, ServiceConfig.class);
		newServConf.setConfigValue(StringUtil.formatedString(newServiceConf.getConfigValue()));
		ServiceConfig savedServConf = servConfRepo
				.findByConfigTypeAndConfigValue(newServConf.getConfigType().name(), newServConf.getConfigValue())
				.orElse(null);
		if (savedServConf != null)
			return ResponseEntity.badRequest().body("Cấu hình này đã được tạo ! Nếu bạn muốn thay đổi giá trị, hãy chuyển sang cập nhật giá trị của nó !");
		if (servConfRepo.findFirstByConfigType(newServConf.getConfigType()) != null && !newServConf.getConfigType().isMultiValue())
			return ResponseEntity.badRequest().body("Cấu hình này chỉ được thiết lập một giá trị !\nNếu bạn muốn thay đổi giá trị, hãy chuyển sang cập nhật giá trị của nó !");

		try {
			savedServConf = servConfRepo.save(newServConf);
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			e.printStackTrace();
			return ResponseEntity.badRequest().body("Có vấn đề gì đó đã xảy ra ! Lưu thất bại ! Hãy thử lại !");
		}
		return ResponseEntity.ok(savedServConf);
	}

	@Transactional
	public ResponseEntity<?> updateServConfigValue(HttpServletRequest request, int serviceConfigId,
			ServiceConfigNewDTO newServiceConf) {

		ServiceConfig existedServConf = servConfRepo.findById(serviceConfigId).orElse(null);

		if (existedServConf == null)
			return ResponseEntity.badRequest().body("Không tìm thấy đối tượng để được cập nhật cấu hình ! Hãy tạo mới đối tượng !");

		newServiceConf.setConfigValue(StringUtil.formatedString(newServiceConf.getConfigValue()));

		if (!newServiceConf.getConfigType().equals(existedServConf.getConfigType()))
			return ResponseEntity.badRequest().body("Chỉ cho phép thay đổi giá trị của cấu hình. Không thay đổi tên cấu hình !\nNếu muốn tạo mới đối tượng hay chuyển sang tạo mới !");
		if(!newServiceConf.getConfigValue().equals(existedServConf.getConfigValue())) {
			ServiceConfig foundedDuplicatedConf = servConfRepo.findByConfigTypeAndConfigValue(
					newServiceConf.getConfigType().name(), newServiceConf.getConfigValue()).orElse(null);
			if (foundedDuplicatedConf != null)
				return ResponseEntity.badRequest().body("Cấu hình này đã tồn tại !");
				existedServConf.setConfigType(newServiceConf.getConfigType());
				existedServConf.setConfigValue(newServiceConf.getConfigValue());
		}
		return ResponseEntity.ok(existedServConf);
	}

	@Transactional
	public ResponseEntity<?> deleteConfigValue(HttpServletRequest request, int serviceConfigId) {
	    ServiceConfig existedServConf = servConfRepo.findById(serviceConfigId).orElse(null);
	    if (existedServConf != null)
		return ResponseEntity.badRequest().body("Not found to delete !");
	    servConfRepo.delete(existedServConf);
	    return ResponseEntity.ok("Deleted Successfully");
	}
	
	
	

}
