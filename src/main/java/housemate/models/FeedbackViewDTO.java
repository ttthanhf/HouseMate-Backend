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
public class FeedbackViewDTO {
	
	private int serviceID;
	
	//private int serviceName; //this will update later
	@JsonInclude(value = Include.NON_NULL)
	private Float avgRating;
	
	@JsonInclude(value = Include.NON_NULL)
	private Integer numOfReview; 
	
	@JsonInclude(value = Include.NON_EMPTY)
	private Map<Integer, Integer> numOfReviewPerRatingLevel;
	
	@JsonInclude(value = Include.NON_NULL)
	private List<FeedbackViewDetailDTO> feedbackList;
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class FeedbackViewDetailDTO{
		private int serviceFeedbackId;
		private int taskId;
		private int customerId;
		private String customerName;
		private String content;
		private LocalDateTime createdAt;
		private Float rating; 
	}
	
	

}
