package housemate.entities;

import org.hibernate.annotations.Where;

import housemate.constants.Enum.AccountStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_account")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Where(clause = "role = 'CUSTOMER'")
public class Customer {
	@Id
	@Column(name = "user_id")
	private int customerId;
	
	@OneToOne
	@JoinColumn(name = "user_id", referencedColumnName = "user_id", updatable = false, insertable = false)
	private UserAccount customerInfo;
	
	@Column(name = "proficiency_score")
	private int profiencyScore;
	
	@Column(name = "account_status")
	@Enumerated(EnumType.STRING)
	private AccountStatus accountStatus;
	
	@Column(name = "is_banned")
	private boolean isBanned;
}
