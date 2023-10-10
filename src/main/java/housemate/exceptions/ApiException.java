package housemate.exceptions;

import org.springframework.http.HttpStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiException extends RuntimeException {
	private HttpStatus code;
	private String messgae;
	
	public ApiException(HttpStatus code, String messgae) {
		super();
		this.code = code;
		this.messgae = messgae;
	}
}
