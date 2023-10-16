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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@Tag(name = "Feedback")
public class FeedbackController {

	@Autowired
	FeedbackService feedbackDao;

	@GetMapping("/feedback/feedb-oview/service/{serviceId}")
	@Operation(summary = "View feedback rating overview in specific service")
	public ResponseEntity<?> getServiceRatingOverview(@PathVariable("serviceId") int serviceId) {
		return feedbackDao.getRatingOverviewByService(serviceId);
	}

	@GetMapping("/feedback/filter/service/{serviceId}")
	@Operation(summary = "Filter feedback base on rating level in specific service")
	public ResponseEntity<?> filterServiceFeedbackByRating(@PathVariable("serviceId") int serviceId,
			@RequestParam("rating") int rating) {
		return feedbackDao.filterServiceFeedbackByRating(serviceId, rating);
	}

	@GetMapping("/feedback/feedb-list/service/{serviceId}")
	@Operation(summary = "Get the list of feedback of specific service")
	public ResponseEntity<?> findAllFeedbackByService(@PathVariable("serviceId") int serviceId) {
		return feedbackDao.findAllFeedbackByService(serviceId);
	}

	@GetMapping("/feedback/{feedback-id}")
	@Operation(summary = "View details of specific service feedback id")
	public ResponseEntity<?> getOne(@PathVariable("feedback-id") int feedbackId) {
		return feedbackDao.getOne(feedbackId);
	}

	@PostMapping("create/feedback/new")
	@Operation(summary = "Create new feedback")
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<?> createNew(HttpServletRequest request, @Valid @RequestBody FeedbackNewDTO newFeedback) {
		return feedbackDao.createNewFeedback(request, newFeedback);
	}

	@PutMapping("update/feedback/{feedback-id}")
	@Operation(summary = "Update existing feedback")
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<?> updateFeedback(
			HttpServletRequest request,
			@Valid @RequestBody FeedbackNewDTO newFeedback,
			@PathVariable("feedback-id") int feedbackId) {
		return feedbackDao.updateFeedback(request, newFeedback, feedbackId);
	}

	@DeleteMapping("delete/feedback/{feedback-id}")
	@Operation(summary = "Remove existing feedback")
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<?> deleteFeedback(@PathVariable("feedback-id") int feedbackId) {
		return feedbackDao.removeFeedback(feedbackId);
	}

}
