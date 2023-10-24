package housemate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import housemate.models.PeriodConfigNewDTO;
import housemate.services.PeriodPriceConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/period-config")
@CrossOrigin
@Tag(name = "Period-config")
@SecurityRequirement(name = "bearerAuth")
public class PeriodPriceConfigController {

	@Autowired
	PeriodPriceConfigService periodConfigService;

	@GetMapping("/inused")
	@Operation(summary = "Get the period price config in used")
	public ResponseEntity<?> getPeriodPriceConfigInUsed() {
		return periodConfigService.getPeriodPriceConfigListInUsed();
	}

	@GetMapping("/all")
	@Operation(summary = "Get the period price config in all")
	public ResponseEntity<?> getPeriodPriceConfigAll() {
		return periodConfigService.getPeriodPriceConfigList();
	}

	@PostMapping
	@Operation(summary = "Create new period config with new config value and config name")
	public ResponseEntity<?> createNewPeriodPriceConfig(HttpServletRequest request,
			@Valid @RequestBody PeriodConfigNewDTO newPeriodConfig) {
		return periodConfigService.createPeriodPriceConfig(request, newPeriodConfig);
	}

	@PutMapping
	@Operation(summary = "Create new period config for the existed config value and config name")
	public ResponseEntity<?> updateNewPeriod(HttpServletRequest request,
			@Valid @RequestBody PeriodConfigNewDTO newPeriodConfig) {
		return periodConfigService.updatePeriodPriceConfig(request, newPeriodConfig);
	}

}
