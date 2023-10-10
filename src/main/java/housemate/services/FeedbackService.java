package housemate.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import housemate.entities.ServiceFeedback;
import housemate.exceptions.UserException;
import housemate.models.FeedbackNewDTO;
import housemate.models.ServiceFeedbackViewDTO;
import housemate.models.ServiceFeedbackViewDTO.ServiceFeedbackViewDetailDTO;
import housemate.repositories.FeedbackRepository;
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
    private AuthorizationUtil authorizationUtil;
	
	ModelMapper mapper = new ModelMapper();
	
	
	public ServiceFeedbackViewDTO getServiceFeedbackOverview(int serviceId) {
		
		 List<ServiceFeedback> serviceFeedbList = 
				 feedBackRepo.findAllByServiceId(serviceId);
		 
		 if(serviceFeedbList.isEmpty() || serviceFeedbList == null)
			 throw new UserException(HttpStatus.NOT_FOUND, "No feedback for this service !");
		
		 ServiceFeedbackViewDTO serviceFeedback = new ServiceFeedbackViewDTO();
		 
		 List<ServiceFeedbackViewDetailDTO> feebackDetailList = new ArrayList<>();
		 for (ServiceFeedback feeback : serviceFeedbList) {
			 ServiceFeedbackViewDetailDTO feedbackViewDetail = mapper.map(feeback, ServiceFeedbackViewDetailDTO.class);

			 //later update
//			 feedbackViewDetail.setCustomerName(userRepo
//					 .findByUserId(feeback.getCustomerId())
//					 .getFullName()
//					 );
			 
			 feebackDetailList.add(feedbackViewDetail);
		}
		
		Map<Integer, Integer> numOfReviewPerRatingLevel = new HashMap<>();
		numOfReviewPerRatingLevel.put(1, feedBackRepo.getNumOfReviewPerRatingLevel(1, serviceId));
		numOfReviewPerRatingLevel.put(2, feedBackRepo.getNumOfReviewPerRatingLevel(2, serviceId));
		numOfReviewPerRatingLevel.put(3, feedBackRepo.getNumOfReviewPerRatingLevel(3, serviceId));
		numOfReviewPerRatingLevel.put(4, feedBackRepo.getNumOfReviewPerRatingLevel(4, serviceId));
		numOfReviewPerRatingLevel.put(5, feedBackRepo.getNumOfReviewPerRatingLevel(5, serviceId)); 
		
		
		serviceFeedback.setServiceID(serviceId);
		serviceFeedback.setAvgRating(feedBackRepo.getFeedbackAvgRating(serviceId));
		serviceFeedback.setNumOfReview(serviceId);
		serviceFeedback.setNumOfReviewPerRatingLevel(numOfReviewPerRatingLevel); 
		serviceFeedback.setFeedbackList(feebackDetailList);
		
		
		return serviceFeedback;
	}
	
	public ServiceFeedbackViewDTO filterServiceFeedbackByRating(int serviceId, int ratingLevel) {
		
		 List<ServiceFeedback> serviceFeedbList = 
				 feedBackRepo.findAllByRating(serviceId, ratingLevel);
		 
		 if(serviceFeedbList.isEmpty() || serviceFeedbList == null)
			 throw new UserException(HttpStatus.NOT_FOUND, "No feedback for this service !");
		
		 ServiceFeedbackViewDTO serviceFeedback = new ServiceFeedbackViewDTO();
		 
		 List<ServiceFeedbackViewDetailDTO> feebackDetailList = new ArrayList<>();
		 for (ServiceFeedback feeback : serviceFeedbList) {
			 ServiceFeedbackViewDetailDTO feedbackViewDetail = mapper.map(feeback, ServiceFeedbackViewDetailDTO.class);
			 //udpate later
//			 feedbackViewDetail.setCustomerName(userRepo
//					 .findByUserId(feeback.getCustomerId())
//					 .getFullName()
//					 );
			 feebackDetailList.add(feedbackViewDetail);
		}
				
		serviceFeedback.setServiceID(serviceId);
		serviceFeedback.setFeedbackList(feebackDetailList);
		
		return serviceFeedback;
	}
	
	public ServiceFeedbackViewDTO findAllFeedbackByService(int serviceId) {
		
		 List<ServiceFeedback> serviceFeedbList = 
				 feedBackRepo.findAllByServiceId(serviceId);
		 
		 if(serviceFeedbList.isEmpty() || serviceFeedbList == null)
			 throw new UserException(HttpStatus.NOT_FOUND, "No feedback for this service !");
		
		 ServiceFeedbackViewDTO serviceFeedback = new ServiceFeedbackViewDTO();
		 
		 List<ServiceFeedbackViewDetailDTO> feebackDetailList = new ArrayList<>();
		 for (ServiceFeedback feeback : serviceFeedbList) {
			 ServiceFeedbackViewDetailDTO feedbackViewDetail = mapper.map(feeback, ServiceFeedbackViewDetailDTO.class);
			 //later update
//			 feedbackViewDetail.setCustomerName(userRepo
//					 .findByUserId(feeback.getCustomerId())
//					 .getFullName()
//					 );
			 feebackDetailList.add(feedbackViewDetail);
		}
				
		serviceFeedback.setServiceID(serviceId);
		serviceFeedback.setFeedbackList(feebackDetailList);
		
		return serviceFeedback;
	}
	
	public List<ServiceFeedback> findAll() {
		
		 List<ServiceFeedback> serviceFeedbList = 
				 feedBackRepo.findAll();
		 
		 if(serviceFeedbList.isEmpty() || serviceFeedbList == null)
			 throw new UserException(HttpStatus.NOT_FOUND, "Empty list now !");
	
		
		return serviceFeedbList;
	}
	 
	
	public ServiceFeedback getOne(int serviceFeedbackId) {
		
		ServiceFeedback feeback =  feedBackRepo.findById(serviceFeedbackId).orElse(null);
		
		if(feeback == null) {
			throw new UserException(HttpStatus.NOT_FOUND, "This feedback does not exists");
		}
		
		return feeback;
	}
	
	public ServiceFeedback createNewFeedback(HttpServletRequest request,FeedbackNewDTO newFeedback) {
		
		int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
		
		ServiceFeedback feedback = mapper.map(newFeedback,ServiceFeedback.class);
		
		feedback.setCreatedAt(LocalDateTime.now());
		feedback.setCustomerId(userId);
		ServiceFeedback addedFeedback =  feedBackRepo.save(feedback);
		
		if(addedFeedback == null) {
			throw new RuntimeException("Saved Failed !");
		}
		
		return addedFeedback;
	}
	
	public ServiceFeedback updateFeedback(HttpServletRequest request, FeedbackNewDTO newFeedback, int serviceFeedbackId) {
		
		int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
		
		ServiceFeedback oldFeedback = feedBackRepo
				.findByServiceFeedbackIdAndServiceIdAndTaskIdAndServiceId(serviceFeedbackId, userId, newFeedback.getTaskId(), newFeedback.getServiceId());
		
		if(oldFeedback == null)
			throw new UserException(HttpStatus.NOT_FOUND, "This feedback does not exist for updating !");
		
		oldFeedback.setContent(newFeedback.getContent());
		oldFeedback.setRating(newFeedback.getRating());
		oldFeedback.setCreatedAt(LocalDateTime.now());
		ServiceFeedback addedFeedback =  feedBackRepo.save(oldFeedback);
		
		if(addedFeedback == null) {
			throw new RuntimeException("Saved Failed !");
		}
		
		return addedFeedback;
	}
	
	
	
	
	
	
	

}
