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
import java.time.format.DateTimeParseException;
import java.util.List;
import org.springframework.http.HttpStatus;

@ControllerAdvice
@RestController
public class GlobalExceptionHandler {

    @ExceptionHandler(BindException.class)
    public ResponseEntity<String> handleValidationException(BindException ex) {
        List<FieldError> fieldErrors = ex.getFieldErrors();

        FieldError firstError = fieldErrors.get(0);
        String errorMessage = firstError.getDefaultMessage();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<String> handleDateTimeParseException(DateTimeParseException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid time format. Please provide date in dd/MM/yyyy format, and time in HH:mm format.");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleConverterException(Exception ex) {
        String mess = ex.getMessage();

        if (mess.contains("not one of the values accepted for Enum class")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body( "Error enum for " +  mess.substring(mess.indexOf("Enum class: [")));
        }

        if (mess.contains("out of range of int")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Numeric value out of range of int. Min: -2147483648, max: 2147483647)");
        }

        if (mess.contains("deserialize value of type `java.time.LocalDate`")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid time format. Please provide date in dd/MM/yyyy format, and time in HH:mm format.");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Convert JSON To Object Failed. Error: " + mess);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
}
