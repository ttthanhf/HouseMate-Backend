package housemate.exceptions;

import java.time.ZonedDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@Builder
//this class will return the object as payload
public class ApiServicesException {

	private final String message;
		
	private final HttpStatus httpStatus;
	
	private final ZonedDateTime timestamp;
	
	
}
