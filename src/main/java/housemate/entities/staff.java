package housemate.entities;

import housemate.constants.Enum.StaffWorkingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "staff")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class staff {

	@Id
	@Column(name = "user_id")
	private int userId;
	
	@Column(name = "profiency_score")
	private int profiencycore;
	
	@Column(name = "avg_rating")
	private float avgRating;
	
	@Column(name = "working_status")
	@Enumerated(EnumType.STRING)
	private StaffWorkingStatus workingStatus; 
}
