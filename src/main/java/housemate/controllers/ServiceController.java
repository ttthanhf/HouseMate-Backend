package housemate.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import housemate.entities.Service;
import housemate.services.TheService;

@RestController
@RequestMapping(path = "api/services")
public class ServiceController {

	@Autowired
	TheService serviceDao ;
	
	@GetMapping
	public ResponseEntity<?> getServiceList(){
		List<Service> serviceList = serviceDao.viewAll();
		if(!serviceList.isEmpty())
			return ResponseEntity.ok(serviceList);
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sorry The List Is Empty !!");
	}
	
	@PostMapping("/new")
	public ResponseEntity<?> addNewService(@RequestBody Service newService){
		//Assume: titleName, salePrice, Description, is full filled) in client side
		//check if title is duplicate
		if(serviceDao.duplicateTitleName(newService.getTitleName()))
			ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Duplicated Title Name !");
		//get the current Admin Id from UserDetails
		Service addedService = serviceDao.createNew(newService);
		return ResponseEntity.ok(addedService);
	}

}
