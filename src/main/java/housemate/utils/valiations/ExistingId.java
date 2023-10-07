package housemate.utils.valiations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.beans.factory.annotation.Autowired;
import housemate.repositories.ServiceRepository;
import housemate.utils.valiations.ExistingId.ExistingServiceIdValidator;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import housemate.constants.Enum.IdType;

@Constraint(validatedBy = ExistingServiceIdValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExistingId {
		
	 IdType type();
	
	String message() default "Invalid id";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
	
	
	public class ExistingServiceIdValidator implements ConstraintValidator<ExistingId, Integer> {
		@Autowired
		ServiceRepository serviceRepo;
		 private IdType type;
		  @Override
		  public void initialize(ExistingId matching) {
		    this.type = matching.type();
		  }
				
		@Override
		public boolean isValid(Integer id, ConstraintValidatorContext context) {
			boolean inValidId;
			switch (type) {
			case SERVICE: 
				
				break;
			default:
				throw new IllegalArgumentException("Unexpected value: " + type);
			}
			
			return inValidId = serviceRepo.findById(id) != null ? true : false;
		}
		

}
	
}
