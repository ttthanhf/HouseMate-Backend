package housemate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@Tag(name = "Feedback")
@CrossOrigin
@RequestMapping("/feedback")
public class FeedbackController {

	@Autowired
	FeedbackService feedbackDao;

	@GetMapping("/service/{service-id}/feedb-oview")
	@Operation(summary = "View feedback rating overview in specific service")
	public ResponseEntity<?> getServiceRatingOverview(@PathVariable("service-id") int serviceId) {
		return feedbackDao.getRatingOverviewByService(serviceId);
	}

	@GetMapping("/service/{service-id}/filter")
	@Operation(summary = "Filter feedback base on rating level in specific service")
	public ResponseEntity<?> filterServiceFeedbackByRating(@PathVariable("service-id") int serviceId,
			@RequestParam("rating") int rating) {
		return feedbackDao.filterServiceFeedbackByRating(serviceId, rating);
	}

	@GetMapping("/service/{service-id}/feedb-list")
	@Operation(summary = "Get the list of feedback of specific service")
	public ResponseEntity<?> findAllFeedbackByService(@PathVariable("service-id") int serviceId) {
		return feedbackDao.findAllFeedbackByService(serviceId);
	}

	@GetMapping("/service/{feedback-id}")
	@Operation(summary = "View details of specific feedback id of any service")
	public ResponseEntity<?> getOne(@PathVariable("feedback-id") int feedbackId) {
		return feedbackDao.getOne(feedbackId);
	}
	
	@GetMapping("/service/top-feedback")
	@Operation(summary = "View top feedback")
	public ResponseEntity<?> getTopFeedback(@RequestParam("rating") int rating) {
		return feedbackDao.findTopFeedback(rating);
	}

	@PostMapping("/new")
	@Operation(summary = "Create new feedback")
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<?> createNew(HttpServletRequest request, @Valid @RequestBody FeedbackNewDTO newFeedback) {
		return feedbackDao.createNewFeedback(request, newFeedback);
	}

	@PutMapping("/{feedback-id}")
	@Operation(summary = "Update existing feedback")
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<?> updateFeedback(
			HttpServletRequest request,
			@Valid @RequestBody FeedbackNewDTO newFeedback,
			@PathVariable("feedback-id") int feedbackId) {
		return feedbackDao.updateFeedback(request, newFeedback, feedbackId);
	}

	@DeleteMapping("/{feedback-id}")
	@Operation(summary = "Remove existing feedback")
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<?> deleteFeedback(HttpServletRequest request, @PathVariable("feedback-id") int feedbackId) {
		return feedbackDao.removeFeedback(request, feedbackId);
	}

}
