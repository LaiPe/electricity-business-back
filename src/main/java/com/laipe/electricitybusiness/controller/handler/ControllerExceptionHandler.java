package com.laipe.electricitybusiness.controller.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Arrays;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(final ResourceNotFoundException exception) {
        log.warn("Caught ResourceNotFoundException: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleSQLIntegrityConstraintViolationException(final SQLIntegrityConstraintViolationException exception) {
        log.warn("Caught SQLIntegrityConstraintViolationException: {}", exception.getMessage());
        // Si SQLIntegrityConstraintViolationException possède une stack de sa stacktrace de type ChargingStationController, on peut supposer que l'erreur vient d'une contrainte liée aux stations de charge
        if (Arrays.stream(exception.getStackTrace()).anyMatch(element -> element.getClassName().contains("ChargingStationController"))) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("This charging station cannot be deleted because there are existing bookings associated with it."));
        }

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("Data constraint violation. Please check required fields, their formats and values."));
    }

    @ExceptionHandler(IntegrityConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleIntegrityConstraintViolationException(final IntegrityConstraintViolationException exception) {
        log.warn("Caught IntegrityConstraintViolationException: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("Caught MethodArgumentNotValidException: {}", ex.getMessage());
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ValidationError(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
        log.warn("Caught BadCredentialsException : {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Invalid username or password"));
    }

    @ExceptionHandler(AlreadyUsedUsernameException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyUsedUsernameException(AlreadyUsedUsernameException ex) {
        log.warn("Caught AlreadyUsedUsernameException : {}", ex.getUsername());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(AlreadyVerifiedUserException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyVerifiedUserException(AlreadyVerifiedUserException ex) {
        log.warn("Caught AlreadyVerifiedUserException : {}", ex.getUserId());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(InvalidVerificationCodeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidVerificationCodeException(InvalidVerificationCodeException ex) {
        log.warn("Caught InvalidVerificationCodeException");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(InvalidBookingState.class)
    public ResponseEntity<ErrorResponse> handleInvalidBookingStateChange(InvalidBookingState ex) {
        log.warn("Caught InvalidBookingStateChange");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Caught IllegalArgumentException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @Getter
    @AllArgsConstructor
    public static class ErrorResponse {
        private String message;
    }

    @Getter
    @AllArgsConstructor
    public static class ValidationError {
        private String field;
        private String message;
    }
}
