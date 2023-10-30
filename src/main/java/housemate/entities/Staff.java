package housemate.entities;

import java.util.List;

import housemate.constants.Enum.StaffWorkingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "staff")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Staff {

	@Id
	@Column(name = "user_id")
	private int userId;
		
	@Column(name = "profiency_score")
	private int profiencyScore;
	
	@Column(name = "avg_rating")
	private float avgRating;
	
	@Column(name = "working_status")
	@Enumerated(EnumType.STRING)
	private StaffWorkingStatus workingStatus;
	
	@OneToOne
	@JoinColumn(name = "user_id", referencedColumnName = "user_id", updatable = false, insertable = false)
	private UserAccount staffInfo;
	
	@Transient
	List<Image> avatars;
}
