package housemate.services;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.Assert;
import housemate.constants.Role;
import housemate.entities.ServiceFeedback;
import housemate.entities.UserAccount;
import housemate.models.FeedbackNewDTO;
import housemate.models.FeedbackViewDTO;
import housemate.models.FeedbackViewDTO.FeedbackViewDetailDTO;
import housemate.repositories.FeedbackRepository;
import housemate.repositories.ImageRepository;
import housemate.repositories.ServiceRepository;
import housemate.repositories.UserRepository;
import housemate.utils.AuthorizationUtil;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class FeedbackService {
    
        @Autowired
        private FeedbackRepository feedBackRepo;
    
        @Autowired
        private UserRepository userRepo;
    
        @Autowired
        private ServiceRepository servRepo;
    
        @Autowired
        private AuthorizationUtil authorizationUtil;
    
        @Autowired
        private ImageRepository imgRepo;
    
        private ModelMapper mapper = new ModelMapper();
    
        private final ZoneId datetimeZone = ZoneId.of("Asia/Ho_Chi_Minh");

	public ResponseEntity<?> getRatingOverviewByService(int serviceId) {

		List<ServiceFeedback> serviceFeedbList = feedBackRepo.findAllByServiceId(serviceId);
		
		if (serviceFeedbList.isEmpty())
		    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chưa có đánh giá tổng quát cho dịch vụ này");

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

		FeedbackViewDTO serviceFeedback = new FeedbackViewDTO();

		if (serviceFeedbList.isEmpty())
		    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chưa có đánh giá nào cho dịch vụ này !");

		List<FeedbackViewDetailDTO> feebackDetailList = new ArrayList<>();
		for (ServiceFeedback feeback : serviceFeedbList) {
			FeedbackViewDetailDTO feedbackViewDetail = mapper.map(feeback, FeedbackViewDetailDTO.class);
			UserAccount customer = userRepo.findByUserId(feeback.getCustomerId());
			feedbackViewDetail.setCustomerName(customer.getFullName());
			feedbackViewDetail.setAvatar(customer.getAvatar());
			feebackDetailList.add(feedbackViewDetail);
		}
		serviceFeedback.setServiceID(serviceId);
		serviceFeedback.setFeedbackList(feebackDetailList);

		return ResponseEntity.ok(serviceFeedback);
	}
	
	public ResponseEntity<?> findTopFeedback(int ratingLevel) {
		List<ServiceFeedback> serviceFeedbList = feedBackRepo.findTopFeedback(ratingLevel);
		if (serviceFeedbList.isEmpty())
			return ResponseEntity.ok(List.of());
		
		List<FeedbackViewDetailDTO> feebackDetailList = new ArrayList<>();

		for (ServiceFeedback feeback : serviceFeedbList) {
			FeedbackViewDetailDTO feedbackViewDetail = mapper.map(feeback, FeedbackViewDetailDTO.class);
			UserAccount customer = userRepo.findByUserId(feeback.getCustomerId());
			feedbackViewDetail.setCustomerName(customer.getFullName());
			feedbackViewDetail.setAvatar(customer.getAvatar());
			feebackDetailList.add(feedbackViewDetail);
		}
		return ResponseEntity.ok(feebackDetailList);
	}

	public ResponseEntity<?> filterServiceFeedbackByRating(int serviceId, int ratingLevel) {
		FeedbackViewDTO serviceFeedback = new FeedbackViewDTO();

		List<ServiceFeedback> serviceFeedbList = feedBackRepo.findAllByRating(serviceId, ratingLevel);
		if (serviceFeedbList.isEmpty())
		    return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body("Không tìm thấy đánh giá " + ratingLevel + " sao cho dịch vụ này !");

		List<FeedbackViewDetailDTO> feebackDetailList = new ArrayList<>();
		for (ServiceFeedback feeback : serviceFeedbList) {
			FeedbackViewDetailDTO feedbackViewDetail = mapper.map(feeback, FeedbackViewDetailDTO.class);
			UserAccount customer = userRepo.findByUserId(feeback.getCustomerId());
			feedbackViewDetail.setCustomerName(customer.getFullName());
			feedbackViewDetail.setAvatar(customer.getAvatar());
			feebackDetailList.add(feedbackViewDetail);
		}
		serviceFeedback.setServiceID(serviceId);
		serviceFeedback.setFeedbackList(feebackDetailList);

		return ResponseEntity.ok(serviceFeedback);
	}

	public ResponseEntity<?> findAll() {
		List<ServiceFeedback> serviceFeedbList = feedBackRepo.findAll();	
		return ResponseEntity.ok(serviceFeedbList);
	}

	public ResponseEntity<?> getOne(int serviceFeedbackId) {
		ServiceFeedback feedback = feedBackRepo.findById(serviceFeedbackId).orElse(null);
		if(feedback == null)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Thông tin về đánh giá này không tồn tại !");
		
		FeedbackViewDetailDTO feedbackViewDetail = mapper.map(feedback, FeedbackViewDetailDTO.class);
		UserAccount customer = userRepo.findByUserId(feedback.getCustomerId());
		feedbackViewDetail.setCustomerName(customer.getFullName());
		feedbackViewDetail.setAvatar(customer.getAvatar());
		
		return ResponseEntity.ok(feedbackViewDetail);
	}

	@Transactional
	public ResponseEntity<?> createNewFeedback(HttpServletRequest request, FeedbackNewDTO newFeedback) {
		ServiceFeedback feedbackToSave = null;
			//TODO: Constraint for comboId taskId, customerId, ServiceId In Here
			//TODO: Allow to create when task status is DONE
			int userId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
			if(feedBackRepo.findByTaskId(newFeedback.getTaskId()) != null)
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Đánh giá này đã được tạo ! Hãy chuyển sang cập nhật nó nếu bạn muốn chỉnh sửa đánh giá nhé  !");
			
			feedbackToSave = mapper.map(newFeedback, ServiceFeedback.class);
			feedbackToSave.setCreatedAt(LocalDateTime.now(datetimeZone));
			feedbackToSave.setCustomerId(userId);
			feedbackToSave = feedBackRepo.save(feedbackToSave);
			if (feedbackToSave == null)
			    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				    .body("Có lỗi đã xảy ra ! Tạo đánh giá thất bại ! Hãy thử tạo lại !");
			servRepo.updateAvgRating(feedbackToSave.getServiceId());
			
		return this.getOne(feedbackToSave.getServiceFeedbackId());
	}

	@Transactional
	public ResponseEntity<?> updateFeedback(HttpServletRequest request, FeedbackNewDTO newFeedback, int serviceFeedbackId) {
		ServiceFeedback feedbackToUpdate = null;

		int currentUserId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
			//TODO: Constraint for comboId taskId, customerId, ServiceId In Here
			//TODO: Allow to create when task status is DONE
			//Only the author of specific feedback is allowed to update include Admin not allow too
			ServiceFeedback oldFeedback = feedBackRepo.findFeedback(serviceFeedbackId, currentUserId, newFeedback.getTaskId(), newFeedback.getServiceId());
			if(oldFeedback == null)
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tồn tại đánh giá này !");
			
			oldFeedback.setContent(newFeedback.getContent());
			oldFeedback.setRating(newFeedback.getRating());
			oldFeedback.setCreatedAt(LocalDateTime.now(datetimeZone));
			feedbackToUpdate = feedBackRepo.save(oldFeedback);
			if (feedbackToUpdate == null)
			    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				    .body("Có lỗi đã xảy ra ! Cập nhật đánh giá thất bại !");			
			servRepo.updateAvgRating(oldFeedback.getServiceId());

		return this.getOne(feedbackToUpdate.getServiceFeedbackId());
	}
	
	@Transactional
	public ResponseEntity<?> removeFeedback(HttpServletRequest request, int serviceFeedbackId) {
		int currentUserId = authorizationUtil.getUserIdFromAuthorizationHeader(request);
		Role currentUserRole = Role.valueOf(authorizationUtil.getRoleFromAuthorizationHeader(request));
		ServiceFeedback feedback = feedBackRepo.findById(serviceFeedbackId).orElse(null);
		if(feedback == null) 
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tồn tại đánh giá này !");
		if (!(currentUserId == feedback.getCustomerId() || currentUserRole.equals(Role.ADMIN))) 
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bạn không có quyền xóa đánh giá này !");
		feedBackRepo.deleteById(serviceFeedbackId);
		servRepo.updateAvgRating(serviceFeedbackId);
		
		return ResponseEntity.ok("Xóa thành công");
	}
}
