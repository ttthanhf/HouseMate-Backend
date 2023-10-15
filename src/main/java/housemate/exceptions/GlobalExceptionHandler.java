/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package housemate.exceptions;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;

@ControllerAdvice
@RestController
public class GlobalExceptionHandler {

    @ExceptionHandler(BindException.class)
    public ResponseEntity<List<String>> handleValidationException(BindException ex) {
        List<String> errors = new ArrayList<>();

        for (FieldError error : ex.getFieldErrors()) {
            errors.add(error.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(errors);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }  
    
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<String> handleConverterException(Exception ex) {
		String mess = ex.getMessage();
		if (mess.contains("not one of the values accepted for Enum class")) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body( "Error enum for " +  mess.substring(mess.indexOf("Enum class: [")));
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Convert Json To Object Faild. Some Error");
	} 

    }


