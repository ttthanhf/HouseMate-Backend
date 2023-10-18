package housemate.controllers;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import housemate.constants.Enum.ServiceField;
import housemate.constants.Enum.SaleStatus;
import housemate.constants.Enum.ServiceCategory;
import housemate.constants.Enum.SortRequired;
import housemate.models.ServiceNewDTO;
import housemate.services.TheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@CrossOrigin
@Tag(name = "Service")
public class ServicesController {
	
	@Autowired
	TheService servDao;
	
	@GetMapping(path = "/services")
	@Operation(summary = "Search service by filter and sort")
	public ResponseEntity<?> filterAndSortAllKind(
			@RequestParam(required = false) Optional<ServiceCategory> category,
			@RequestParam(required = false) Optional<SaleStatus> saleStatus,
			@RequestParam(required = false) Optional<Integer> rating,
			@RequestParam(required = false) Optional<ServiceField> sortBy,
			@RequestParam(required = false) Optional<SortRequired> orderBy,
			@RequestParam(required = false) Optional<Integer> page) {
		return servDao.searchFilterAllKind("", category, saleStatus, rating, sortBy, orderBy, page);
	}

	@GetMapping(path = "/services/search")
	@Operation(summary = "Search service by keyword, filter, sort")
	public ResponseEntity<?> searchAll(
			@RequestParam(required = true) String keyword,
			@RequestParam(required = false) Optional<ServiceCategory> category,
			@RequestParam(required = false) Optional<SaleStatus> saleStatus,
			@RequestParam(required = false) Optional<Integer> rating,
			@RequestParam(required = false) Optional<ServiceField> sortBy,
			@RequestParam(required = false) Optional<SortRequired> orderBy,
			@RequestParam(required = false) Optional<Integer> page) {
		return servDao.searchFilterAllKind(keyword, category, saleStatus, rating, sortBy, orderBy, page);
	}
	
	@GetMapping(path = "/services/topsale")
	@Operation(summary = "Search service by filter and sort")
	public ResponseEntity<?> getTopsale(){
		return servDao.getTopsale();
	}
	
	@GetMapping(path = "/services/single")
	@Operation(summary = "This will help you to get single service list only")
	public ResponseEntity<?> getAllSingleService(){
		return servDao.getAllSingleService();
	}
	
	@GetMapping(path = "/services/{id}")
	@Operation(summary = "Get one service and view in details")
	public ResponseEntity<?> getOne(@PathVariable int id) {
		return servDao.getOne(id);
	}
	
	@PostMapping(path = "service/new")
	@Operation(summary = "Create new service")
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<?> createNewService(HttpServletRequest request, @Valid @RequestBody ServiceNewDTO newServiceDTO) {	
		return servDao.createNew(request, newServiceDTO);
	}
	
	@PutMapping(path = "/service/{id}")
	@Operation(summary = "Update existing services")
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<?> updateService(
			HttpServletRequest request, 
		    @PathVariable("id") int serviceId,
			@Valid @RequestBody ServiceNewDTO newServiceDTO) {
		return servDao.updateInfo(request, serviceId, newServiceDTO);
	}
	
	@GetMapping(path = "/service/all-kind")
	@Operation(summary = "get the list of all kind of service")
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<?> getAllKind(HttpServletRequest request) {
		return servDao.getAllKind(request);
	}

}
