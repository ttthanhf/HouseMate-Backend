package housemate.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import housemate.constants.SaleStatus;
import housemate.entities.Service;
import housemate.entities.ServiceFeedback;
import housemate.exceptions.ApiServicesRequestException;
import housemate.repositories.ServiceFeedbackRepository;
import housemate.services.TheService;

@RestController
@RequestMapping(path = "/api/services")
public class ServiceController {

	@Autowired
	TheService serviceDao;
	
	@Autowired
	ServiceFeedbackRepository serviceFeedbackRepo;

	@GetMapping
	public ResponseEntity<?> getServiceList() {
		List<Service> serviceList = serviceDao.getAll();
		if (serviceList.isEmpty())
			// throw new ApiServicesRequestException("Sorry The List Is Empty Now");
			return ResponseEntity.status(HttpStatus.NO_CONTENT)
					.body("Sorry The List Is Empty Or You Input Wrong Field !");

		return ResponseEntity.ok(serviceList);
	}
	
	@GetMapping("/servic-feedbacks")
	public ResponseEntity<?> getFeedback() {
		List<ServiceFeedback> serviceList = serviceFeedbackRepo.findAll();
		if (serviceList.isEmpty())
			// throw new ApiServicesRequestException("Sorry The List Is Empty Now");
			return ResponseEntity.status(HttpStatus.NO_CONTENT)
					.body("Sorry The Feedback Is Empty Or You Input Wrong Field !");

		return ResponseEntity.ok(serviceList);
	}

	@GetMapping("/sort")
	public ResponseEntity<?> getServiceListSort(@RequestParam String fieldname, 
			@RequestParam String requireOrder) {
		List<Service> serviceList = serviceDao.sortByOneField(fieldname, requireOrder);
		
		if (serviceList == null)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("The Data Field You Requested To Sort Does Not Match !");

		return ResponseEntity.ok(serviceList);
	}

	@GetMapping("/keyname-search")
	public ResponseEntity<?> getServiceListByKeywordName(String keyword) {
		List<Service> serviceList = serviceDao.searchByName(keyword.trim());
		
		if (serviceList.isEmpty() || serviceList == null)
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Service Not Found Matching Your Keyword");

		return ResponseEntity.ok(serviceList);
	}

	@GetMapping("/sale-status-filter")
	public ResponseEntity<?> filterBySaleStatus(SaleStatus saleStatus) {
		List<Service> serviceList = serviceDao.filterBySaleStatus(saleStatus);
		
		if (serviceList.isEmpty() || serviceList == null)
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Sorry The List Is Empty");

		return ResponseEntity.ok(serviceList);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> viewOneService(@PathVariable("id") int serviceId) {
		Service service = serviceDao.getOne(serviceId);
		
		if (service == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("This Service Does Not Exist");

		return ResponseEntity.ok(service);
	}

	@PutMapping("/new-sale-status/{id}")
	public ResponseEntity<?> updateServiceSaleStatus(@PathVariable("id") int serviceId,
			@RequestBody SaleStatus saleStatus) {
		Service service = serviceDao.updateSaleStatus(serviceId, saleStatus);
		
		if (service == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("This Service Does Not Exist To Be Updated");

		return ResponseEntity.ok(service);
	}

	@PutMapping("/new-info/{id}")
	public ResponseEntity<?> updateServiceInfo(@PathVariable("id") int serviceId, 
			@RequestBody Service serviceNewInfo) {
		if (!serviceNewInfo.getTitleName().isEmpty()) {
			if (serviceDao.duplicateTitleName(serviceNewInfo.getTitleName()))
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Duplicated Title Name !");
		}
		Service updatedService = serviceDao.updateInfo(serviceId, serviceNewInfo);

		if (updatedService == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Service You Want To Update Not Exist !");

		return ResponseEntity.ok(updatedService);
	}

	@PostMapping("/new")
	public ResponseEntity<?> addNewService(@RequestBody Service newService) {
		// Assume: titleName, salePrice, Description, is full filled) in client side
		// check if title is duplicate

		if (serviceDao.duplicateTitleName(newService.getTitleName()))
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Duplicated Title Name !");
		// get the current Admin Id from UserDetails

		Service addedService = serviceDao.createNew(newService);

		if (addedService == null)
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Added Failed !");

		return ResponseEntity.ok(addedService);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> removeOneService(@PathVariable("id") int serviceId) {
		Service delService = serviceDao.removeOne(serviceId);
		if (delService == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The Service Does Not Exist To Be Removed !");
		
		return ResponseEntity.ok("Delete Done !");
	}

}
