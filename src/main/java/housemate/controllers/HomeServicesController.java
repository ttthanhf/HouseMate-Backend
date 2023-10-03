package housemate.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import housemate.constants.Enum.ServiceField;
import housemate.constants.Enum.SaleStatus;
import housemate.constants.Enum.SortRequired;
import housemate.entities.PackageService;
import housemate.entities.PackageService.Summary;
import housemate.entities.Service;
import housemate.repositories.PackageServiceRepository;
import housemate.repositories.ServiceRepository;
import housemate.services.ThePackageService;
import housemate.services.TheService;

@RestController
@RequestMapping("/housemate-services")
public class HomeServicesController {

	@Autowired
	ServiceRepository servRepo;
	@Autowired
	PackageServiceRepository packRepo;
	
	@Autowired
	TheService servDao;
	@Autowired
	ThePackageService packDao;
	
	@GetMapping
	public ResponseEntity<?> getAllServiceAndPackagae() {
		List<Service> serviceList = servDao.getAll();
		List<PackageService.Summary> packageList = packDao.getAll();
		
		if(serviceList.isEmpty() && packageList.isEmpty())
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Empty Services Now !");
		
		Map<String, Object> response = new HashMap<>();
	    response.put("serviceList", serviceList);
	    response.put("packageList", packageList);

	    return ResponseEntity.ok(response);
	}
	
	@GetMapping("/filter")
	public ResponseEntity<?> filter(@RequestParam("filter") String filter) {
		Map<String, Object> response = new HashMap<>();
		//filter only 2 type : singleServices || package
		if(filter.equals("single")) {
			List<Service> serviceList = servDao.getAll();
			response.put("serviceList", serviceList);
		}
			List<PackageService.Summary> packageList = packDao.getAll();
			response.put("packageList", packageList);
		
			
	    return ResponseEntity.ok(response);
	}
	
	@GetMapping("/available-status")
	public ResponseEntity<?> getAvailbleServiceAndPackagae1() {
		List<Service> serviceList = servDao.filterBySaleStatus(SaleStatus.AVAILABLE);
		List<PackageService.Summary> packageList = packDao.filterBySaleStatus(SaleStatus.AVAILABLE);
		
		if(serviceList.isEmpty() && packageList.isEmpty())
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Empty Services Now !");
		
		Map<String, Object> response = new HashMap<>();
	    response.put("serviceList", serviceList);
	    response.put("packageList", packageList);

	    return ResponseEntity.ok(response);
	}
	
	@GetMapping("/keyname-search")
	public ResponseEntity<?> searchServiceAndPackagae(String keyword) {
		List<Service> serviceList = servDao.searchByName(keyword.trim());
		List<PackageService.Summary> packageList = packDao.searchByName(keyword.trim());
		
		if (packageList.isEmpty() && serviceList.isEmpty())
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Not Found !");

		Map<String, Object> response = new HashMap<>();
	    response.put("serviceList", serviceList);
	    response.put("packageList", packageList);
	    
		return ResponseEntity.ok(response);
	}

	@GetMapping("/rating")
	public ResponseEntity<?> filterByRating(@RequestParam("rating") int requiredRating) {
		List<Service> serviceList = servDao.filterByRating(requiredRating);
		List<PackageService.Summary> packageList = packDao.filterByRating(requiredRating);
		if (packageList.isEmpty() && serviceList.isEmpty())
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Empty List !");

		Map<String, Object> response = new HashMap<>();
	    response.put("serviceList", serviceList);
	    response.put("packageList", packageList);
	    
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/sort")
	public ResponseEntity<?> getPackageServiceListSort(@RequestParam ServiceField fieldname, 
			@RequestParam SortRequired requireOrder) {
		List<Service> serviceList = servDao.sortByOneField(fieldname, requireOrder);
		List<PackageService.Summary> packageList = packDao.sortByOneField(fieldname, requireOrder);
		
		if (packageList == null && serviceList == null )
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Does Not Match Any Field !");

		Map<String, Object> response = new HashMap<>();
	    response.put("serviceList", serviceList);
	    response.put("packageList", packageList);
	    
		return ResponseEntity.ok(response);
	}
	
	
}
