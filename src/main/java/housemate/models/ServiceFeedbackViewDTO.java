package housemate.models;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceFeedbackViewDTO {
	
	private int serviceID;
	
	//private int serviceName; //this will update later
	@JsonInclude(value = Include.NON_EMPTY)
	private int avgRating;
	
	@JsonInclude(value = Include.NON_EMPTY)
	private int numOfReview; 
	
	@JsonInclude(value = Include.NON_EMPTY)
	private Map<Integer, Integer> numOfReviewPerRatingLevel;
	
	private List<ServiceFeedbackViewDetailDTO> feedbackList;
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ServiceFeedbackViewDetailDTO{
		private int serviceFeedbackId;
		private String customerName;
		private String content;
		private LocalDateTime createdAt;
		private Float rating; 
	}
	
	

}
