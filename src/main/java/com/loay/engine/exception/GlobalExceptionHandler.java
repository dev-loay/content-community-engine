package com.loay.engine.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildProblemDetail(HttpStatus.NOT_FOUND, "Resource Not Found", ex.getMessage(), request);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ProblemDetail handleDuplicateResource(DuplicateResourceException ex, HttpServletRequest request) {
        return buildProblemDetail(HttpStatus.CONFLICT, "Duplicate Resource", ex.getMessage(), request);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ProblemDetail handleBusinessRule(BusinessRuleException ex, HttpServletRequest request) {
        return buildProblemDetail(HttpStatus.BAD_REQUEST, "Business Rule Violation", ex.getMessage(), request);
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    public ProblemDetail handleForbiddenOperation(ForbiddenOperationException ex, HttpServletRequest request) {
        return buildProblemDetail(HttpStatus.FORBIDDEN, "Access Denied", ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ProblemDetail problemDetail = buildProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Validation Failed",
                "One or more fields have invalid values",
                request
        );

        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        problemDetail.setProperty("errors", fieldErrors);
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error occurred at {}: ", request.getRequestURI(), ex);
        return buildProblemDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "An unexpected error occurred. Please try again later.",
                request
        );
    }

    private ProblemDetail buildProblemDetail(HttpStatus status, String title, String detail, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}
