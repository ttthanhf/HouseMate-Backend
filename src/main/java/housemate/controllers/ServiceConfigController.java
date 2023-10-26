package housemate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import housemate.constants.ServiceConfiguration;
import housemate.models.ServiceConfigNew;
import housemate.services.ServiceConfigService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/service-config")
@CrossOrigin
@Tag(name = "config")
@SecurityRequirement(name = "bearerAuth")
public class ServiceConfigController {
	
	@Autowired
	ServiceConfigService servConfDao;
	
	@GetMapping
	public ResponseEntity<?> viewAllServiceConfig(){
		return servConfDao.getAll();
	}
	
	@GetMapping("/{type}")
	public ResponseEntity<?> viewAllByConfigType(@RequestParam ServiceConfiguration configType){
		return servConfDao.getAllByServiceConfigType(configType);
	}
	
	@PostMapping("/new")
	public ResponseEntity<?> createNewConfigValue(@RequestParam ServiceConfigNew serviceConfigNew){
		return servConfDao.createNewServConf(serviceConfigNew);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<?> updateConfigValue(@PathVariable("id") int serviceConfigId, @RequestParam ServiceConfigNew serviceConfigNew){
		return servConfDao.updateServConf(serviceConfigId, serviceConfigNew);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteConfigValue(@PathVariable("id") int serviceConfigId){
		return servConfDao.deleteConfigValue(serviceConfigId);
	}
	
	

}
