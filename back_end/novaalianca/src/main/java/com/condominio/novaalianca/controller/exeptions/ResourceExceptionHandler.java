package com.condominio.novaalianca.controller.exeptions;

import com.condominio.novaalianca.services.exceptions.DataBaseException;
import com.condominio.novaalianca.services.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ResourceExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> entityNotFound(ResourceNotFoundException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, e.getMessage());
        problemDetail.setType(ErrorConstants.RESOURCE_NOT_FOUND_TYPE);
        problemDetail.setTitle(ErrorConstants.RESOURCE_NOT_FOUND_TITLE);
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", Instant.now());
        
        return ResponseEntity.status(status).body(problemDetail);
    }

    @ExceptionHandler(DataBaseException.class)
    public ResponseEntity<ProblemDetail> database(DataBaseException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, e.getMessage());
        problemDetail.setType(ErrorConstants.DATABASE_ERROR_TYPE);
        problemDetail.setTitle(ErrorConstants.DATABASE_ERROR_TITLE);
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", Instant.now());
        
        return ResponseEntity.status(status).body(problemDetail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> validation(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, "Um ou mais campos contêm dados inválidos.");
        problemDetail.setType(ErrorConstants.VALIDATION_ERROR_TYPE);
        problemDetail.setTitle(ErrorConstants.VALIDATION_ERROR_TITLE);
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", Instant.now());
        
        Map<String, String> invalidFields = new HashMap<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            invalidFields.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        problemDetail.setProperty("invalidFields", invalidFields);

        return ResponseEntity.status(status).body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> generalException(Exception e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, e.getMessage());
        problemDetail.setType(ErrorConstants.INTERNAL_SERVER_ERROR_TYPE);
        problemDetail.setTitle(ErrorConstants.INTERNAL_SERVER_ERROR_TITLE);
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(status).body(problemDetail);
    }
}
