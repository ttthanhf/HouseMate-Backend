package housemate.services;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import housemate.entities.ServiceFeedback;
import housemate.entities.UserAccount;
import housemate.models.FeedbackNewDTO;
import housemate.models.FeedbackViewDTO;
import housemate.models.FeedbackViewDTO.FeedbackViewDetailDTO;
import housemate.repositories.FeedbackRepository;
import housemate.repositories.ServiceRepository;
import housemate.repositories.UserRepository;
import housemate.utils.AuthorizationUtil;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class FeedbackService {
	@Autowired
	FeedbackRepository feedBackRepo;

	@Autowired
	UserRepository userRepo;
	
	@Autowired
	ServiceRepository servRepo;

	@Autowired
	AuthorizationUtil authorizationUtil;

	ModelMapper mapper = new ModelMapper();

	public ResponseEntity<?> getRatingOverviewByService(int serviceId) {

		List<ServiceFeedback> serviceFeedbList = feedBackRepo.findAllByServiceId(serviceId);

		if (serviceFeedbList.isEmpty() || serviceFeedbList == null)
			return ResponseEntity.badRequest().body("No feedback for this service can be found !");

		FeedbackViewDTO serviceFeedback = new FeedbackViewDTO();

		Map<Integer, Integer> numOfReviewPerRatingLevel = new HashMap<>();
		numOfReviewPerRatingLevel.put(1, feedBackRepo.getNumOfReviewPerRatingLevel(serviceId, 1));
		numOfReviewPerRatingLevel.put(2, feedBackRepo.getNumOfReviewPerRatingLevel(serviceId, 2));
		numOfReviewPerRatingLevel.put(3, feedBackRepo.getNumOfReviewPerRatingLevel(serviceId, 3));
		numOfReviewPerRatingLevel.put(4, feedBackRepo.getNumOfReviewPerRatingLevel(serviceId, 4));
		numOfReviewPerRatingLevel.put(5, feedBackRepo.getNumOfReviewPerRatingLevel(serviceId, 5));

		serviceFeedback.setServiceID(serviceId);
		serviceFeedback.setAvgRating(feedBackRepo.getFeedbackAvgRating(serviceId));
		serviceFeedback.setNumOfReview(feedBackRepo.getNumOfReview(serviceId));
		serviceFeedback.setNumOfReviewPerRatingLevel(numOfReviewPerRatingLevel);

		return ResponseEntity.ok(serviceFeedback);
	}

	public ResponseEntity<?> findAllFeedbackByService(int serviceId) {

		List<ServiceFeedback> serviceFeedbList = feedBackRepo.findAllByServiceId(serviceId);

		if (serviceFeedbList.isEmpty() || serviceFeedbList == null)
			return ResponseEntity.badRequest().body("No feedback for this service can be found !");

		FeedbackViewDTO serviceFeedback = new FeedbackViewDTO();

		List<FeedbackViewDetailDTO> feebackDetailList = new ArrayList<>();
		for (ServiceFeedback feeback : serviceFeedbList) {
			FeedbackViewDetailDTO feedbackViewDetail = mapper.map(feeback, FeedbackViewDetailDTO.class);
			UserAccount customer = userRepo.findByUserId(feeback.getCustomerId());
			feedbackViewDetail.setCustomerName(customer == null ? "Anonymous" : customer.getFullName());
			feebackDetailList.add(feedbackViewDetail);
		}

		serviceFeedback.setServiceID(serviceId);
		serviceFeedback.setFeedbackList(feebackDetailList);

		return ResponseEntity.ok(serviceFeedback);
	}

	public ResponseEntity<?> filterServiceFeedbackByRating(int serviceId, int ratingLevel) {

		List<ServiceFeedback> serviceFeedbList = feedBackRepo.findAllByRating(serviceId, ratingLevel);

		if (serviceFeedbList.isEmpty() || serviceFeedbList == null)
			return ResponseEntity.badRequest().body("No feedback for this service with rating " + ratingLevel + " can be found !");

		FeedbackViewDTO serviceFeedback = new FeedbackViewDTO();

		List<FeedbackViewDetailDTO> feebackDetailList = new ArrayList<>();
		for (ServiceFeedback feeback : serviceFeedbList) {
			FeedbackViewDetailDTO feedbackViewDetail = mapper.map(feeback, FeedbackViewDetailDTO.class);
			UserAccount customer = userRepo.findByUserId(feeback.getCustomerId());
			feedbackViewDetail.setCustomerName(customer == null ? "Anonymous" : customer.getFullName());
			feebackDetailList.add(feedbackViewDetail);
		}

		serviceFeedback.setServiceID(serviceId);
		serviceFeedback.setFeedbackList(feebackDetailList);

		return ResponseEntity.ok(serviceFeedback);
	}

	public ResponseEntity<?> findAll() {

		List<ServiceFeedback> serviceFeedbList = feedBackRepo.findAll();

		if (serviceFeedbList.isEmpty() || serviceFeedbList == null)
			return ResponseEntity.badRequest().body("Empty list now !");

		return ResponseEntity.ok(serviceFeedbList);
	}

	public ResponseEntity<?> getOne(int serviceFeedbackId) {

		ServiceFeedback feeback = feedBackRepo.findById(serviceFeedbackId).orElse(null);

		if (feeback == null)
			return ResponseEntity.badRequest().body("This feedback does not exist");

		return ResponseEntity.ok(feeback);
	}

	@Transactional
	public ResponseEntity<?> createNewFeedback(HttpServletRequest request, FeedbackNewDTO newFeedback) {

		int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
		
		//TODO: Constraint for comboId taskId, customerId, ServiceId In Here
		
		if(feedBackRepo.findByCustomerIdAndTaskIdAndServiceId(userId, newFeedback.getTaskId(), newFeedback.getServiceId()) != null) 
			return ResponseEntity.badRequest().body("This feedback has existed ! Not allow to create new ! Only allow to update !");

		ServiceFeedback feedback = mapper.map(newFeedback, ServiceFeedback.class);
		feedback.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
		feedback.setCustomerId(userId);
		ServiceFeedback addedFeedback = feedBackRepo.save(feedback);

		if (addedFeedback == null)
			return ResponseEntity.badRequest().body("This feedback does not exists");
		
		servRepo.updateAvgRating(feedback.getServiceId());
		
		return ResponseEntity.ok(addedFeedback);
	}

	@Transactional
	public ResponseEntity<?> updateFeedback(HttpServletRequest request, FeedbackNewDTO newFeedback, int serviceFeedbackId) {

		int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
		
		//TODO: Constraint for comboId taskId, customerId, ServiceId In Here

		ServiceFeedback oldFeedback = feedBackRepo.findFeedback(serviceFeedbackId, userId, newFeedback.getTaskId(), newFeedback.getServiceId());

		if (oldFeedback == null)
			return ResponseEntity.badRequest().body("This feedback does not exist for updating !");

		oldFeedback.setContent(newFeedback.getContent());
		oldFeedback.setRating(newFeedback.getRating());
		oldFeedback.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
		ServiceFeedback addedFeedback = feedBackRepo.save(oldFeedback);

		if (addedFeedback == null) 
			return ResponseEntity.badRequest().body("Update Failed !");
		
		servRepo.updateAvgRating(oldFeedback.getServiceId());
		
		return ResponseEntity.ok(addedFeedback);
	}
	
	@Transactional
	public ResponseEntity<?> removeFeedback(int serviceFeedbackId) {

		ServiceFeedback feedback = feedBackRepo.findById(serviceFeedbackId).orElse(null);

		if (feedback == null)
			return ResponseEntity.badRequest().body("This feedback does not exist for removing !");

		feedBackRepo.deleteById(serviceFeedbackId);
		servRepo.updateAvgRating(serviceFeedbackId);
		
		return ResponseEntity.ok("Remove Successfully");
	}

}
