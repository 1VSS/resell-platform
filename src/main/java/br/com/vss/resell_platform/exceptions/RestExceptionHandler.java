package br.com.vss.resell_platform.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Global exception handler for REST API exceptions
 */
@ControllerAdvice
public class RestExceptionHandler {

    /**
     * Handle username already taken exception
     */
    @ExceptionHandler(UsernameAlreadyTakenException.class)
    public ResponseEntity<RestErrorMessage> usernameAlreadyTakenHandler(UsernameAlreadyTakenException exception) {
        String errors = exception.getMessage();
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

        RestErrorMessage errorMessage = new RestErrorMessage(status, List.of(errors));

        return ResponseEntity.status(status).body(errorMessage);
    }

    /**
     * Handle validation exceptions from @Valid annotations
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestErrorMessage> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

        RestErrorMessage errorResponse = new RestErrorMessage(status, errors);

        return ResponseEntity.status(status).body(errorResponse);
    }

    /**
     * Handle email already taken exception
     */
    @ExceptionHandler(EmailAlreadyTakenException.class)
    public ResponseEntity<RestErrorMessage> emailAlreadyTakenHandler(EmailAlreadyTakenException exception) {
        String errors = exception.getMessage();
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

        RestErrorMessage errorResponse = new RestErrorMessage(status, List.of(errors));

        return ResponseEntity.status(status).body(errorResponse);
    }

    /**
     * Handle item not found exception
     */
    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<RestErrorMessage> itemNotFoundHandler(ItemNotFoundException exception) {
        String errors = exception.getMessage();
        HttpStatus status = HttpStatus.NOT_FOUND;

        RestErrorMessage errorResponse = new RestErrorMessage(status, List.of(errors));

        return ResponseEntity.status(status).body(errorResponse);
    }

    /**
     * Handle invalid owner exception
     */
    @ExceptionHandler(InvalidOwnerException.class)
    public ResponseEntity<RestErrorMessage> invalidOwnerHandler(InvalidOwnerException exception) {
        String errors = exception.getMessage();
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        RestErrorMessage errorResponse = new RestErrorMessage(status, List.of(errors));

        return ResponseEntity.status(status).body(errorResponse);
    }

    /**
     * Handle item not available exception
     */
    @ExceptionHandler(ItemNotAvailableException.class)
    public ResponseEntity<RestErrorMessage> itemNotAvailableHandler(ItemNotAvailableException exception) {
        String errors = exception.getMessage();
        HttpStatus status = HttpStatus.CONFLICT;

        RestErrorMessage errorResponse = new RestErrorMessage(status, List.of(errors));

        return ResponseEntity.status(status).body(errorResponse);
    }
    
    /**
     * Handle resource not found exception
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<RestErrorMessage> resourceNotFoundHandler(ResourceNotFoundException exception) {
        String errors = exception.getMessage();
        HttpStatus status = HttpStatus.NOT_FOUND;

        RestErrorMessage errorResponse = new RestErrorMessage(status, List.of(errors));

        return ResponseEntity.status(status).body(errorResponse);
    }
    
    /**
     * Handle resource already exists exception
     */
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<RestErrorMessage> resourceAlreadyExistsHandler(ResourceAlreadyExistsException exception) {
        String errors = exception.getMessage();
        HttpStatus status = HttpStatus.CONFLICT;

        RestErrorMessage errorResponse = new RestErrorMessage(status, List.of(errors));

        return ResponseEntity.status(status).body(errorResponse);
    }
    
    /**
     * Handle NoSuchElementException for when Optional.get() is called on an empty Optional
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<RestErrorMessage> noSuchElementHandler(NoSuchElementException exception) {
        String errors = "Resource not found";
        HttpStatus status = HttpStatus.NOT_FOUND;

        RestErrorMessage errorResponse = new RestErrorMessage(status, List.of(errors));

        return ResponseEntity.status(status).body(errorResponse);
    }
    
    /**
     * Fallback handler for all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestErrorMessage> handleGlobalException(Exception exception, WebRequest request) {
        String errors = "An unexpected error occurred";
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        RestErrorMessage errorResponse = new RestErrorMessage(status, List.of(errors));

        return ResponseEntity.status(status).body(errorResponse);
    }
}
