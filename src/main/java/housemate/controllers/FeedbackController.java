package housemate.controllers;


import org.springframework.beans.factory.annotation.Autowired;
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
import housemate.models.FeedbackNewDTO;
import housemate.services.FeedbackService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/feedbacks")
@Tag(name = "Feedback")
public class FeedbackController {

	@Autowired
	FeedbackService feedbackDao;

	@GetMapping
	public ResponseEntity<?> getServiceFeedbackList() {
		return feedbackDao.findAll();
	}

	@GetMapping("feedb-oview/service/{serviceId}")
	public ResponseEntity<?> getServiceRatingOverview(@PathVariable("serviceId") int serviceId) {
		return feedbackDao.getRatingOverviewByService(serviceId);
	}

	@GetMapping("filter/service/{serviceId}")
	public ResponseEntity<?> filterServiceFeedbackByRating(@PathVariable("serviceId") int serviceId, @RequestParam("rating") int rating) {
		return feedbackDao.filterServiceFeedbackByRating(serviceId, rating);
	}

	@GetMapping("feedb-list/service/{serviceId}")
	public ResponseEntity<?> findAllFeedbackByService(@PathVariable("serviceId") int serviceId) {
		return feedbackDao.findAllFeedbackByService(serviceId);
	}

	@GetMapping("{feedback-id}")
	public ResponseEntity<?> getOne(@PathVariable("feedback-id") int serviceFeedbackId) {
		return feedbackDao.getOne(serviceFeedbackId);
	}

	@PostMapping("/new")
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<?> createNew(HttpServletRequest request, @Valid @RequestBody FeedbackNewDTO newFeedback) {
		return feedbackDao.createNewFeedback(request, newFeedback);
	}

	@PutMapping("{feedback-id}")
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<?> updateFeedback(HttpServletRequest request, @Valid @RequestBody FeedbackNewDTO newFeedback, @PathVariable("feedback-id") int serviceFeedbackId) {
		return feedbackDao.updateFeedback(request, newFeedback, serviceFeedbackId);
	}
	
	@DeleteMapping("{feedback-id}")
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<?> deleteFeedback(@PathVariable("feedback-id") int serviceFeedbackId) {
		return feedbackDao.removeFeedback(serviceFeedbackId);
	}

}
