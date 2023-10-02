package housemate.constants.validations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import housemate.repositories.PackageServiceRepository;
import housemate.repositories.ServiceRepository;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import housemate.constants.validations.UniqueTitleName.UniqueTitleNameValidator;

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
		@Autowired
		PackageServiceRepository packageRepo;
		
		@Override
		public boolean isValid(String value, ConstraintValidatorContext context) {
			
			boolean isValidTilteName = true;
			try {
				if((packageRepo.findByTitleNameIgnoreCase(value.trim()) != null) 
					    || (serviceRepo.findByTitleNameIgnoreCase(value.trim()) != null)) {
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


