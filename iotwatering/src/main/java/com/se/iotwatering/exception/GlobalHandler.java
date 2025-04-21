package com.se.iotwatering.exception;

import com.se.iotwatering.dto.http.response.ResponseAPITemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalHandler {
    private ResponseEntity<ResponseAPITemplate<String>> handleException(Exception e, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(ResponseAPITemplate.<String>builder()
                        .code(status.value())
                        .message(e.getMessage())
                        .result(null)
                        .build());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
    @ExceptionHandler(WebServerException.class)
    public ResponseEntity<ResponseAPITemplate<String>> handleWebServerException(
            WebServerException e, HttpServletRequest request) {
    
        ErrorCode code = e.getErrorCode();
        return ResponseEntity.status(code.getHttpStatus())
                .body(ResponseAPITemplate.<String>builder()
                        .code(code.getCode())
                        .message(code.getMessage())
                        .status(code.getHttpStatus().value())
                        .path(request.getRequestURI())
                        .timestamp(Instant.now().toString())
                        .result(null)
                        .build());
    }
    

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseAPITemplate<String>> handleWebServerException(IllegalArgumentException e) {
        return handleException(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseAPITemplate<String>> handleAccessDeniedException(AccessDeniedException e) {
        return handleException(e, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthenticationServiceException.class)
    public ResponseEntity<ResponseAPITemplate<String>> handleAuthenticationServiceException(AuthenticationServiceException e) {
        return handleException(e, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseAPITemplate<String>> handleException(Exception e) {
        log.error("Unexpected exception", e);
        return handleException(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ClientAbortException.class)
    public ResponseEntity<ResponseAPITemplate<String>> handleClientAbortException(ClientAbortException e) {
        log.warn("Client disconnected unexpectedly, ignoring...");
        return handleException(e, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
