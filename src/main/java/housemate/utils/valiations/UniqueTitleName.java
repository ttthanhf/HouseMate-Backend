package housemate.utils.valiations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.beans.factory.annotation.Autowired;
import housemate.repositories.ServiceRepository;
import housemate.utils.valiations.UniqueTitleName.UniqueTitleNameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

@Constraint(validatedBy = UniqueTitleNameValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UniqueTitleName {

	/*resolve a message in case of violation*/
	String message() default "The title name of service or package must not be duplicated !";
	/*define under which circumstances this validation is to be triggered*/
	Class<?>[] groups() default {};
	/*define a payload to be passed with this validation (rarly used feature*/
	Class<? extends Payload>[] payload() default {};
	
	
	
	public class UniqueTitleNameValidator  implements ConstraintValidator<UniqueTitleName, String> {

		@Autowired
		ServiceRepository serviceRepo;
		
		@Override
		public boolean isValid(String value, ConstraintValidatorContext context) {
			
			boolean isValidTilteName = true;
			try {
				if((serviceRepo.findByTitleNameIgnoreCase(value) != null))
				{
						isValidTilteName = false;
						throw new Exception("Duplicated Title Name");
				}
			}catch(Exception ex) {
				ex.printStackTrace();
			}
			
			return isValidTilteName;
		}

}
}


