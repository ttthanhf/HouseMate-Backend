package housemate.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import housemate.constants.Enum.IdType;
import housemate.constants.Enum.ServiceField;
import housemate.constants.Enum.SaleStatus;
import housemate.constants.Enum.SortRequired;
import housemate.constants.validations.ExistingId;
import housemate.entities.PackageService;
import housemate.mappers.PackageServiceMapper;
import housemate.models.PackageServiceDTO;
import housemate.services.ThePackageService;
import jakarta.validation.Valid;

@RestController("/api/package-service")
@RequestMapping(path = "/api/package-service")
public class PackageServiceController {
	
	@Autowired
	ThePackageService packageServiceDao;
	
	
	@GetMapping()
	public ResponseEntity<?> getAll() {
		List<PackageService.Summary> packageServiceList = packageServiceDao.getAll();
		if (packageServiceList.isEmpty())
			// throw new ApiServicesRequestException("Sorry The List Is Empty Now");
			return ResponseEntity.status(HttpStatus.NO_CONTENT)
					.body("Empty List !");

		return ResponseEntity.ok(packageServiceList);
	}
	
	@GetMapping("/all-in-details")
	public ResponseEntity<?> getPackageServiceListInDetails() {
		List<PackageService.Summary> packageServiceList = packageServiceDao.getAll();
		if (packageServiceList.isEmpty())
			// throw new ApiServicesRequestException("Sorry The List Is Empty Now");
			return ResponseEntity.status(HttpStatus.NO_CONTENT)
					.body("Empty List !");

		return ResponseEntity.ok(packageServiceList);
	}
	
	@GetMapping("/available-status")
	public ResponseEntity<?> getAvailblePackageServiceList() {
		List<PackageService.Summary> packageServiceList = packageServiceDao.filterBySaleStatus(SaleStatus.AVAILABLE);
		if (packageServiceList.isEmpty())
			// throw new ApiServicesRequestException("Sorry The List Is Empty Now");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Empty List !");

		return ResponseEntity.ok(packageServiceList);
	}
	
	@GetMapping("/rating")
	public ResponseEntity<?> filterByRating(@RequestParam("rating") int requiredRating) {
		List<PackageService.Summary> packageServiceList = packageServiceDao.filterByRating(requiredRating);
		if (packageServiceList.isEmpty())
			// throw new ApiServicesRequestException("Sorry The List Is Empty Now");
			return ResponseEntity.status(HttpStatus.NO_CONTENT)
					.body("Empty List !");

		return ResponseEntity.ok(packageServiceList);
	}

	@GetMapping("/sort")
	public ResponseEntity<?> getPackageServiceListSort(@RequestParam ServiceField fieldname, 
			@RequestParam SortRequired requireOrder) {
		List<PackageService.Summary> packageServiceList = packageServiceDao.sortByOneField(fieldname, requireOrder);
		
		if (packageServiceList == null)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Does Not Match Any Field !");

		return ResponseEntity.ok(packageServiceList);
	}

	@GetMapping("/keyname-search")
	public ResponseEntity<?> getPackageServiceListByKeywordName(String keyword) {
		List<PackageService.Summary> packageServiceList = packageServiceDao.searchByName(keyword.trim());
		
		if (packageServiceList.isEmpty() || packageServiceList == null)
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Not Found !");

		return ResponseEntity.ok(packageServiceList);
	}

	@GetMapping("/sale-status-filter")
	public ResponseEntity<?> filterBySaleStatus(SaleStatus saleStatus) {
		List<PackageService.Summary> serviceList = packageServiceDao.filterBySaleStatus(saleStatus);
		
		if (serviceList.isEmpty() || serviceList == null)
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Empty List !");

		return ResponseEntity.ok(serviceList);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> viewOnePackageService(@ExistingId(type = IdType.PACKAGE) @PathVariable("id") int packageServiceId) {
		PackageService packageService = packageServiceDao.getOne(packageServiceId);
		
		if (packageService == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found !");

		return ResponseEntity.ok(packageService);
	}

	//@PostMapping("/new")
	public ResponseEntity<?> addNewPackageService(@Valid @RequestBody PackageServiceDTO packageDto, Errors validRequest) {


		 if (validRequest.hasErrors()) {
		        String errorMessage = validRequest.getFieldError().getDefaultMessage();
		        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
		    }
		 
		PackageService newPackageService = PackageServiceMapper.mapFrNewPackageServiceDTO(packageDto);
				
		PackageService addedPackaged = packageServiceDao.createNew(newPackageService);

		if (addedPackaged == null)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Added Failed !");

		return ResponseEntity.ok(addedPackaged);
	}
	
	//@PutMapping("/new-info/{id}")
	public ResponseEntity<?> updatePackageServiceInfo(
			@PathVariable("id") int packageServiceId, 
			@Valid @RequestBody PackageServiceDTO packageServiceDto, Errors validRequest) {
		
		if (validRequest.hasErrors()) {
	        String errorMessage = validRequest.getFieldError().getDefaultMessage();
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
	    }
		
		PackageService newPackageServiceInfo = PackageServiceMapper.mapFrNewPackageServiceDTO(packageServiceDto);
		
		PackageService updatedPackagedService = packageServiceDao.updateInfo(packageServiceId, newPackageServiceInfo);

		if (updatedPackagedService == null)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Updated Failed !");

		return ResponseEntity.ok(updatedPackagedService);
	}
	
	@PutMapping("/new-sale-status/{id}")
	public ResponseEntity<?> updatePackageServiceSaleStatus(
			@PathVariable("id") int packageServiceId,
			 @RequestParam SaleStatus saleStatus
		) {
		 
		PackageService packageService = packageServiceDao.updateSaleStatus(packageServiceId, saleStatus);
		
		if (packageService == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found !");

		return ResponseEntity.ok(packageService);
	}





	@DeleteMapping("/{id}")
	public ResponseEntity<?> removeOnePackageService(@PathVariable("id") int serviceId) {
		PackageService delPackageService = packageServiceDao.removeOne(serviceId);
		if (delPackageService == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The Service Does Not Exist To Be Removed !");
		
		return ResponseEntity.ok("Delete Done !");
	}
}
