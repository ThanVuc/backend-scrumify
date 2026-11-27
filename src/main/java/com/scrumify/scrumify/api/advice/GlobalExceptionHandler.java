package com.scrumify.scrumify.api.advice;

import com.scrumify.scrumify.api.dto.response.common.ApiErrorResponse;
import com.scrumify.scrumify.api.dto.response.common.ValidationErrorResponse;
import com.scrumify.scrumify.domain.exception.BusinessValidationException;
import com.scrumify.scrumify.domain.exception.ConflictException;
import com.scrumify.scrumify.domain.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ValidationErrorResponse.FieldError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ValidationErrorResponse.FieldError(error.getField(), error.getDefaultMessage()))
                .toList();
        return buildValidationResponse(errors, request, ex);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ValidationErrorResponse> handleMethodValidation(
            HandlerMethodValidationException ex, HttpServletRequest request) {
        List<ValidationErrorResponse.FieldError> errors = ex.getParameterValidationResults().stream()
                .flatMap(result -> {
                    String field = result.getMethodParameter().getParameterName();
                    return result.getResolvableErrors().stream()
                            .map(error -> new ValidationErrorResponse.FieldError(field, error.getDefaultMessage()));
                })
                .toList();
        return buildValidationResponse(errors, request, ex);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {
        List<ValidationErrorResponse.FieldError> errors = ex.getConstraintViolations().stream()
                .map(violation -> {
                    String path = violation.getPropertyPath().toString();
                    int dotIndex = path.lastIndexOf('.');
                    String field = dotIndex >= 0 ? path.substring(dotIndex + 1) : path;
                    return new ValidationErrorResponse.FieldError(field, violation.getMessage());
                })
                .toList();
        return buildValidationResponse(errors, request, ex);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ValidationErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        List<ValidationErrorResponse.FieldError> errors = List.of(
                new ValidationErrorResponse.FieldError(ex.getName(), "Invalid value for " + ex.getName()));
        return buildValidationResponse(errors, request, ex);
    }

    private ResponseEntity<ValidationErrorResponse> buildValidationResponse(
            List<ValidationErrorResponse.FieldError> errors, HttpServletRequest request, Exception ex) {
        log.warn("Validation failed for request {}: {}", request.getRequestURI(), errors, ex);
        String traceId = MDC.get("traceId");
        ValidationErrorResponse body = new ValidationErrorResponse(
                "VALIDATION_ERROR", errors, request.getRequestURI(), traceId);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(String code, String detail, HttpServletRequest request, HttpStatus status, Exception ex) {
        log.warn("Handling exception for request {}: {}", request.getRequestURI(), code, ex);
        String traceId = MDC.get("traceId");
        ApiErrorResponse body = new ApiErrorResponse(code, detail, request.getRequestURI(), traceId);
        return ResponseEntity.status(status).body(body);
    }
}
