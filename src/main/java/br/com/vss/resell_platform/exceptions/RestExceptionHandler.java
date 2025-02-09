package br.com.vss.resell_platform.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(UsernameAlreadyTakenException.class)
    public ResponseEntity<RestErrorMessage> usernameAlreadyTakenHandler(UsernameAlreadyTakenException exception) {

        String errors = exception.getMessage();
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

        RestErrorMessage errorMessage = new RestErrorMessage(status, List.of(errors));

        return ResponseEntity.status(status).body(errorMessage);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestErrorMessage> handleValidation(MethodArgumentNotValidException ex) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

        var errorResponse = new RestErrorMessage(status, errors);

        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(EmailAlreadyTakenException.class)
    public ResponseEntity<RestErrorMessage> emailAlreadyTakenHandler(EmailAlreadyTakenException exception) {

        String errors = exception.getMessage();
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

        RestErrorMessage errorResponse = new RestErrorMessage(status, List.of(errors));

        return ResponseEntity.status(status).body(errorResponse);
    }

}
