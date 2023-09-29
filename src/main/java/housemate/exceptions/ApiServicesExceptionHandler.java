package housemate.exceptions;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice //tell spring this class will be served for exceptionHandling
public class ApiServicesExceptionHandler {

	
	@ExceptionHandler(value = {ApiServicesRequestException.class}) 
	//this class will handle exceptions from ApiSerciesRequestException 
	//if catching multiple exceptions throw exception name class in the value
	public ResponseEntity<Object> handleApiServicesExceptionHandler(ApiServicesRequestException e){
		//1. Create payload containing exception details
		HttpStatus badRequest = HttpStatus.BAD_REQUEST;
		ApiServicesException apiServicesException =  new ApiServicesException(
				e.getMessage(),
				badRequest,
				ZonedDateTime.now(ZoneId.of("Z"))
				);
		//2. Return response entity
		return new ResponseEntity<>(apiServicesException, badRequest);
	}
}
