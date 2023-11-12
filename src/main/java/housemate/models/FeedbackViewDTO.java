package housemate.models;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import housemate.entities.Image;
import housemate.entities.Service;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackViewDTO {

	private int serviceID;

	// private int serviceName; //this will update later
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
	public static class FeedbackViewDetailDTO {
		private int serviceFeedbackId;
	    private Service service;
		private int taskId;
		private int customerId;
		private String customerName;
		private String content;
		private LocalDateTime createdAt;
		private Float rating;
		private String avatar;
	}

}
