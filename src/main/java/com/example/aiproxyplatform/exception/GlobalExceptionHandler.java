package com.example.aiproxyplatform.exception;

import com.example.aiproxyplatform.dto.ErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleRateLimit(RateLimitExceededException ex) {
        ErrorResponse body = buildError(
                HttpStatus.TOO_MANY_REQUESTS.value(),
                "Too Many Requests",
                ex.getMessage(),
                ex.getRetryAfterSeconds()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.RETRY_AFTER, String.valueOf(ex.getRetryAfterSeconds()));

        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .headers(headers)
                .body(body);
    }

    @ExceptionHandler(QuotaExceededException.class)
    public ResponseEntity<ErrorResponse> handleQuotaExceeded(QuotaExceededException ex) {
        return ResponseEntity
                .status(HttpStatus.PAYMENT_REQUIRED)
                .body(buildError(
                        HttpStatus.PAYMENT_REQUIRED.value(),
                        "Payment Required",
                        ex.getMessage(),
                        null
                ));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(buildError(
                        HttpStatus.NOT_FOUND.value(),
                        "Not Found",
                        ex.getMessage(),
                        null
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildError(
                        HttpStatus.BAD_REQUEST.value(),
                        "Bad Request",
                        ex.getMessage(),
                        null
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String firstMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Validation failed");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildError(
                        HttpStatus.BAD_REQUEST.value(),
                        "Bad Request",
                        firstMessage,
                        null
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Internal Server Error",
                        "An unexpected error occurred.",
                        null
                ));
    }

    private ErrorResponse buildError(int status, String error, String message, Long retryAfterSeconds) {
        return ErrorResponse.builder()
                .status(status)
                .error(error)
                .message(message)
                .timestamp(LocalDateTime.now())
                .retryAfterSeconds(retryAfterSeconds)
                .build();
    }
}
