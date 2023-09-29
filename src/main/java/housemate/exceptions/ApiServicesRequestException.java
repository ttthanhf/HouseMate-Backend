package housemate.exceptions;

public class ApiServicesRequestException extends RuntimeException{
	
	public ApiServicesRequestException(String message) {
		super(message);
	}

	public ApiServicesRequestException(String message, Throwable cause) {
		super(message, cause);
	}
}
