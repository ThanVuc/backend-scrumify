package com.scrumify.scrumify.api.advice;

import com.scrumify.scrumify.api.error.ApiErrorResponse;
import com.scrumify.scrumify.domain.exception.BusinessValidationException;
import com.scrumify.scrumify.domain.exception.ConflictException;
import com.scrumify.scrumify.domain.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildResponse(ex.getCode(), ex.getDetail(), request, HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(ConflictException ex, HttpServletRequest request) {
        return buildResponse(ex.getCode(), ex.getDetail(), request, HttpStatus.CONFLICT, ex);
    }

    @ExceptionHandler(BusinessValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessValidation(BusinessValidationException ex, HttpServletRequest request) {
        return buildResponse(ex.getCode(), ex.getDetail(), request, HttpStatus.UNPROCESSABLE_ENTITY, ex);
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(String code, String detail, HttpServletRequest request, HttpStatus status, Exception ex) {
        log.warn("Handling exception for request {}: {}", request.getRequestURI(), code, ex);
        String traceId = MDC.get("traceId");
        ApiErrorResponse body = new ApiErrorResponse(code, detail, request.getRequestURI(), traceId);
        return ResponseEntity.status(status).body(body);
    }
}
