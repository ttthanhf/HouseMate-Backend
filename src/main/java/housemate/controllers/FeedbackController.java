package housemate.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import housemate.entities.ServiceFeedback;
import housemate.models.FeedbackNewDTO;
import housemate.models.ServiceFeedbackViewDTO;
import housemate.services.FeedbackService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/feedbacks")
public class FeedbackController {
	
	@Autowired
	FeedbackService feedBackDao;
	
	@GetMapping
	public ResponseEntity<?> getServiceFeedbackList(){
		List<ServiceFeedback> serviceFeedBList = feedBackDao.findAll();
		return ResponseEntity.ok().body(serviceFeedBList);
	}
	
	@GetMapping("feedb-oview/service/{id}")
	public ResponseEntity<?> getServiceFeedBackOverview(@PathVariable("id") int serviceId){
		ServiceFeedbackViewDTO serviceFeedBOverview = feedBackDao.getServiceFeedbackOverview(serviceId);
		return ResponseEntity.ok().body(serviceFeedBOverview);
	}
	
	@GetMapping("filter/service/{id}")
	public ResponseEntity<?> filterServiceFeedbackByRating(@PathVariable("id") int serviceId, @RequestParam("rating") int rating){
		ServiceFeedbackViewDTO serviceFeedBOverview = feedBackDao.filterServiceFeedbackByRating(serviceId, rating);
		return ResponseEntity.ok().body(serviceFeedBOverview);
	}
	
	@GetMapping("feedb-list/service/{id}")
	public ResponseEntity<?> findAllFeedbackByService(@PathVariable("id") int serviceId){
		ServiceFeedbackViewDTO serviceFeedBack = feedBackDao.findAllFeedbackByService(serviceId);
		return ResponseEntity.ok().body(serviceFeedBack);
	}
	
	@GetMapping("{feedback-id}")
	public ResponseEntity<?> getOne(@PathVariable("feedback-id") int serviceFeedbackId){
		ServiceFeedback feedBack = feedBackDao.getOne(serviceFeedbackId);
		return ResponseEntity.ok().body(feedBack);
	}
	
	@PostMapping("/new")
	public ResponseEntity<?> createNew(HttpServletRequest request, @RequestBody FeedbackNewDTO newFeedback ){
		ServiceFeedback feedBack = feedBackDao.createNewFeedback(request, newFeedback);
		return ResponseEntity.ok().body(feedBack);
	}
	
	@PutMapping("{feedback-id}")
	public ResponseEntity<?> updateFeedback(HttpServletRequest request, FeedbackNewDTO newFeedback, @PathVariable("feedback-id") int serviceFeedbackId ){
		ServiceFeedback feedBack = feedBackDao.updateFeedback(request, newFeedback, serviceFeedbackId);
		return ResponseEntity.ok().body(feedBack);
	}

}
