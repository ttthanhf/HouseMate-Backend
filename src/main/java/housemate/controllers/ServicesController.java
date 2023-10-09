package housemate.controllers;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
import housemate.exceptions.GlobalExceptionHandler;
import housemate.models.ServiceNewDTO;
import housemate.models.ServiceViewDTO;
import housemate.repositories.ServiceRepository;
import housemate.services.TheService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/services")
public class ServicesController {

	@Autowired
	ServiceRepository servRepo;
	
	@Autowired
	TheService servDao;
	
	@GetMapping() //category = "package", "single"
	public ResponseEntity<?> filterAndSortAllKind(
			@RequestParam(required = false) Optional<ServiceCategory> category,
			@RequestParam(required = false) Optional<SaleStatus>  saleStatus,
			@RequestParam(required = false) Optional<Integer> rating,
			@RequestParam(required = false) Optional<ServiceField> sortBy,
			@RequestParam(required = false) Optional<SortRequired> orderBy) {

		ServiceCategory cateogryValue = category.orElse(ServiceCategory.general);
		SaleStatus statusValue = saleStatus.orElse(SaleStatus.AVAILABLE); 
		Integer ratingValue = rating.orElse(0); 
		ServiceField sort = sortBy.orElse(ServiceField.PRICE);
		SortRequired order = orderBy.orElse(SortRequired.ASC);
		
		List<Service> serviceList =
				servDao.fitlerAndSortAllKind(cateogryValue, statusValue, ratingValue, sort, order);

		if (serviceList.isEmpty() || serviceList == null )
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not Found !");
		return ResponseEntity.ok(serviceList);
	}
	
	@GetMapping("/search") //category = "package", "single"
	public ResponseEntity<?> searchAll(
			@RequestParam(required = true) String keyword,
			@RequestParam(required = false) Optional<ServiceCategory> category,
			@RequestParam(required = false) Optional<SaleStatus> saleStatus,
			@RequestParam(required = false) Optional<Integer> rating,
			@RequestParam(required = false) Optional<ServiceField> sortBy,
			@RequestParam(required = false) Optional<SortRequired> orderBy) {

		ServiceCategory cateogryValue = category.orElse(ServiceCategory.general);
		SaleStatus statusValue = saleStatus.orElse(SaleStatus.AVAILABLE); 
		Integer ratingValue = rating.orElse(0); 
		ServiceField sort = sortBy.orElse(ServiceField.PRICE);
		SortRequired order = orderBy.orElse(SortRequired.ASC);
		
		List<Service> serviceList =
				servDao.searchAllKind(keyword, cateogryValue, statusValue, ratingValue, sort, order);

		if (serviceList.isEmpty() || serviceList == null )
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not Found !");
		return ResponseEntity.ok(serviceList);
	}
	
	@GetMapping("/all")
	public ResponseEntity<?> getAll() {
		List<Service> serviceList = servDao.getAllAvailable();
		
		if(serviceList.isEmpty())
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Empty Services Now !");
		
	    return ResponseEntity.ok(serviceList);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getOne(@PathVariable int id) {
		ServiceViewDTO service = servDao.getOne(id);
		
		if(service == null)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The Service Not Exist");
		
	    return ResponseEntity.ok(service);
	}
	
	@PostMapping("/new")
	public ResponseEntity<?> createNewService(@Valid @RequestBody ServiceNewDTO newServiceDTO) {	
		ServiceViewDTO service = null;
		try {
			service = servDao.createNew(newServiceDTO);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok(service);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<?> updateService(@PathVariable("id") int serviceId,@Valid @RequestBody ServiceNewDTO newServiceDTO) {	
		ServiceViewDTO service = null;
		try {
			service = servDao.updateInfo(serviceId, newServiceDTO);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok(service);
	}
	

	
	

	
	
	
}
