package exceptions;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;

@ControllerAdvice
public class UserExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return new ResponseEntity<>(buildErrorMessage(extractValidationMessage(ex), HttpStatus.BAD_REQUEST, request), status);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @org.springframework.web.bind.annotation.ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CustomErrorMessage> handleConstraintViolationException(
        ConstraintViolationException ex, WebRequest request) {
        var violations = ex.getConstraintViolations();
        StringBuilder builder = new StringBuilder();
        String errorMessage = violations.stream()
            .map(violation -> builder.append(" " + violation.getMessage()))
            .toString();
        return new ResponseEntity<>(buildErrorMessage(errorMessage, HttpStatus.BAD_REQUEST, request), HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(InsufficientPasswordException.class)
    public ResponseEntity<CustomErrorMessage> handleInsufficientPasswordException(
        InsufficientPasswordException ex, WebRequest request) {
        return new ResponseEntity<>(buildErrorMessage(ex.getMessage(), HttpStatus.BAD_REQUEST, request), HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(UserExistsException.class)
    public ResponseEntity<CustomErrorMessage> handleUserExistsException(
        UserExistsException ex, WebRequest request) {
        return new ResponseEntity<>(buildErrorMessage(ex.getMessage(), HttpStatus.BAD_REQUEST, request), HttpStatus.BAD_REQUEST);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(InvalidChangeException.class)
    public ResponseEntity<CustomErrorMessage> handleInvalidChangeException(
        InvalidChangeException ex, WebRequest request) {
        return new ResponseEntity<>(buildErrorMessage(ex.getMessage(), HttpStatus.BAD_REQUEST, request), HttpStatus.BAD_REQUEST);
    }


    @org.springframework.web.bind.annotation.ExceptionHandler(AuthorizationViolationException.class)
    public ResponseEntity<CustomErrorMessage> handleAuthorizationViolationException(
        AuthorizationViolationException ex, HttpStatus status, WebRequest request) {
        return new ResponseEntity<>(buildErrorMessage(ex.getMessage(), HttpStatus.FORBIDDEN, request), status);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(NotFoundException.class)
    public ResponseEntity<CustomErrorMessage> handleNotFoundException(
        NotFoundException ex, WebRequest request){
        return new ResponseEntity<>(buildErrorMessage(ex.getMessage(), HttpStatus.NOT_FOUND, request), HttpStatus.NOT_FOUND);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(AccessDeniedException.class)
    public void handleAccessDeniedException (HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.FORBIDDEN.value(), "Access Denied!");
    }

    public String extractValidationMessage(Exception ex) {
        String exceptionMessage = ex.getMessage();
        String[] messageParts = exceptionMessage.split(";");
        String validationMessage = messageParts[messageParts.length - 1];
        return validationMessage.trim().replaceAll("default message \\[|]]", "");
    }

    public CustomErrorMessage buildErrorMessage(String errorMessage, HttpStatus status, WebRequest request) {
        return new CustomErrorMessage(
            LocalDateTime.now(),
            status.value(),
            status.getReasonPhrase(),
            errorMessage,
            request.getDescription(false).trim().replaceAll("uri=", ""));
    }



}
