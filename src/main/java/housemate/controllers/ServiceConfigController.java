package housemate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import housemate.constants.Enum.ServiceConfiguration;
import housemate.models.ServiceConfigNewDTO;
import housemate.services.ServiceConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/service-config")
@CrossOrigin
@Tag(name = "Config")
@SecurityRequirement(name = "bearerAuth")
public class ServiceConfigController {
	
	@Autowired
	ServiceConfigService servConfDao;
	
	@GetMapping
	@Operation(summary = "View the collection config values of specific service config type")
	public ResponseEntity<?> viewAllServiceConfig(){
		return servConfDao.getAll();
	}
	
	@GetMapping("/type")
	@Operation(summary = "View the collection of each service config type")
	public ResponseEntity<?> viewAllByConfigType(@RequestParam ServiceConfiguration configType){
		return servConfDao.getAllByServiceConfigType(configType);
	}
	
	@PostMapping("/new")
	@Operation(summary = "Create new config value for the specific config type")
	public ResponseEntity<?> createNewConfigValue(HttpServletRequest request, @Valid @RequestBody ServiceConfigNewDTO serviceConfigNew){
		return servConfDao.createNewServConfig(request, serviceConfigNew);
	}
	
	@PutMapping("/{id}")
	@Operation(summary = "Update new config value for the specific config type")
	public ResponseEntity<?> updateConfigValue(HttpServletRequest request, @PathVariable("id") int serviceConfigId, @Valid @RequestBody ServiceConfigNewDTO serviceConfigNew){
		return servConfDao.updateServConfigValue(request, serviceConfigId, serviceConfigNew);
	}
	
	@DeleteMapping("/{id}")
	@Operation(summary = "Delete config value ")
	public ResponseEntity<?> deleteConfigValue(HttpServletRequest request, @PathVariable("id") int serviceConfigId){
		return servConfDao.deleteConfigValue(request, serviceConfigId);
	}
}
