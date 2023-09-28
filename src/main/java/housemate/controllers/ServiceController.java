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
import housemate.services.TheService;

@RestController
@RequestMapping(path = "/api/services")
public class ServiceController {

	@Autowired
	TheService serviceDao ;
	

	@GetMapping
	public ResponseEntity<?> getServiceList(){
		List<Service> serviceList = serviceDao.getAll();
		if(serviceList.isEmpty())
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sorry The List Is Empty !!");
		
		return ResponseEntity.ok(serviceList); 
	}
	
	@GetMapping("/sort")
	public ResponseEntity<?> getServiceListSort(@RequestParam String fieldname, @RequestParam String requireOrder){
		List<Service> serviceList = serviceDao.sortByOneField(fieldname, requireOrder);
		if(serviceList == null)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sorry The List Is Empty Or You Input Wrong Field!!");
		
		return ResponseEntity.ok(serviceList); 
	}
	
	@GetMapping("/keyname-search")
	public ResponseEntity<?> getServiceListByKeywordName(String keyword){
		List<Service> serviceList = serviceDao.searchByName(keyword.trim());
		if(serviceList.isEmpty() || serviceList == null )
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not Found Any Service Match The Keyword");
		
		return ResponseEntity.ok(serviceList);

	}
	
	@GetMapping("/sale-status-filter")
	public ResponseEntity<?> filterBySaleStatus(SaleStatus saleStatus){
		List<Service> serviceList = serviceDao.filterBySaleStatus(saleStatus);
		if(serviceList.isEmpty() || serviceList == null )
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sorry The List Is Empty");
			
		return ResponseEntity.ok(serviceList);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> viewOneService(@PathVariable("id") int serviceId){
		Service service = serviceDao.getOne(serviceId);
		if(service == null)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sorry The Service Not Exist");
		
		return ResponseEntity.ok(service);
	}
	
	@PutMapping("/new-sale-status/{id}")
	public ResponseEntity<?> updateServiceSaleStatus(@PathVariable("id") int serviceId, @RequestBody SaleStatus saleStatus){
		Service service = serviceDao.updateSaleStatus(serviceId, saleStatus);
		if(service == null)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sorry The Service Do Not Exists To Update");
		
		return ResponseEntity.ok(service);
	}
	
	@PutMapping("/new-info/{id}")
	public ResponseEntity<?> updateServiceInfo(@PathVariable("id") int serviceId, @RequestBody Service serviceNewInfo){
		if(!serviceNewInfo.getTitleName().isEmpty()) {
			if(serviceDao.duplicateTitleName(serviceNewInfo.getTitleName()))
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Duplicated Title Name !");
			
		}
		Service updatedService = serviceDao.updateInfo(serviceId, serviceNewInfo);
		
		if(updatedService == null) 
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Service You Want To Update Not Exist !");
		return ResponseEntity.ok(updatedService);
	}
	
	@PostMapping("/new")
	public ResponseEntity<?> addNewService(@RequestBody Service newService){
		//Assume: titleName, salePrice, Description, is full filled) in client side
		//check if title is duplicate
		
		if(serviceDao.duplicateTitleName(newService.getTitleName()))
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Duplicated Title Name !");
		//get the current Admin Id from UserDetails
		
		Service addedService = serviceDao.createNew(newService);
		return ResponseEntity.ok(addedService);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> removeOneService(@PathVariable("id") int serviceId){
			if (serviceDao.getOne(serviceId) == null) 
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The Service Does Not Exist To Remove !");

		serviceDao.removeOne(serviceId);
		return ResponseEntity.ok("Delete Done !");
	}

}
