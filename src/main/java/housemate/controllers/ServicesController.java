package housemate.controllers;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import housemate.constants.Enum.ServiceField;
import housemate.constants.Enum.SaleStatus;
import housemate.constants.Enum.ServiceCategory;
import housemate.constants.Enum.SortRequired;
import housemate.entities.Service;
import housemate.models.ServiceNewDTO;
import housemate.models.ServiceViewDTO;
import housemate.repositories.ServiceRepository;
import housemate.services.TheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/services")
@Tag(name = "Service")
public class ServicesController {

	@Autowired
	ServiceRepository servRepo;
	
	@Autowired
	TheService servDao;
	
	@GetMapping 
	@Operation(summary = "Search service by filter and sort")
	public ResponseEntity<?> filterAndSortAllKind(
			@RequestParam(required = false) Optional<ServiceCategory> category,
			@RequestParam(required = false) Optional<SaleStatus>  saleStatus,
			@RequestParam(required = false) Optional<Integer> rating,
			@RequestParam(required = false) Optional<ServiceField> sortBy,
			@RequestParam(required = false) Optional<SortRequired> orderBy) 
					 {
		List<Service> serviceList;
		serviceList = servDao.searchFilterAllKind(null, category, saleStatus, rating, sortBy, orderBy);
		return ResponseEntity.ok(serviceList);
	}
	
	@GetMapping("/search") 
	@Operation(summary = "Search service by keyword, filter, sort")
	public ResponseEntity<?> searchAll(
			@RequestParam(required = true) String keyword,
			@RequestParam(required = false) Optional<ServiceCategory> category,
			@RequestParam(required = false) Optional<SaleStatus> saleStatus,
			@RequestParam(required = false) Optional<Integer> rating,
			@RequestParam(required = false) Optional<ServiceField> sortBy,
			@RequestParam(required = false) Optional<SortRequired> orderBy) 
					{
		List<Service> serviceList;
		serviceList = servDao.searchFilterAllKind(keyword, category, saleStatus, rating, sortBy, orderBy);
		return ResponseEntity.ok(serviceList);
	}
	
	@GetMapping("/all")
	@Operation(summary = "Get the list of all services")
	public ResponseEntity<?> getAll() {
		List<Service> serviceList = servDao.getAllAvailable();
	    return ResponseEntity.ok(serviceList);
	}
	
	@GetMapping("/{id}")
	@Operation(summary = "Get one service and view in details")
	public ResponseEntity<?> getOne(@PathVariable int id) {
		ServiceViewDTO service = servDao.getOne(id);
	    return ResponseEntity.ok(service);
	}
	
	@PostMapping("/new")
	@Operation(summary = "Create new service")
	public ResponseEntity<?> createNewService(@Valid @RequestBody ServiceNewDTO newServiceDTO) {	
		ServiceViewDTO service = servDao.createNew(newServiceDTO);
		return ResponseEntity.ok(service);
	}
	
	@PutMapping("/{id}")
	@Operation(summary = "Update existing services")
	public ResponseEntity<?> updateService(
			@PathVariable("id") int serviceId,
			@Valid @RequestBody ServiceNewDTO newServiceDTO) {	
		ServiceViewDTO service = servDao.updateInfo(serviceId, newServiceDTO);
		return ResponseEntity.ok(service);
	}
	

	
	

	
	
	
}
