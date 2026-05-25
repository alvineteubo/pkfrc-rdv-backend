package com.pkrfc.rdv_backend.exceptions;

import com.pkrfc.rdv_backend.models.dtos.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiResponse<String>> handleResourceNotFound(
            ResourceNotFoundException ex, WebRequest request) {
        log.error("404 NOT FOUND - {}", ex.getMessage());
        return new ResponseEntity<>(
                new ApiResponse<>(false, ex.getMessage(),
                        request.getDescription(false), new Date()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse<String>> handleBadRequest(
            BadRequestException ex, WebRequest request) {
        log.error("400 BAD REQUEST - {}", ex.getMessage());
        return new ResponseEntity<>(
                new ApiResponse<>(false, ex.getMessage(),
                        request.getDescription(false), new Date()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(DuplicateDataException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiResponse<String>> handleDuplicateData(
            DuplicateDataException ex, WebRequest request) {
        log.error("409 CONFLICT - {}", ex.getMessage());
        return new ResponseEntity<>(
                new ApiResponse<>(false, ex.getMessage(),
                        request.getDescription(false), new Date()),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err -> {
            log.info("Validation error - {} : {}", err.getField(), err.getDefaultMessage());
            errors.put(err.getField(), err.getDefaultMessage());
        });
        log.error("400 VALIDATION ERROR");
        return new ResponseEntity<>(
                new ApiResponse<>(false, "Des champs sont invalides", errors, new Date()), HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiResponse<String>> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, WebRequest request) {
        log.error("409 CONFLICT (contrainte DB) - {}", ex.getMostSpecificCause().getMessage());
        return new ResponseEntity<>(
                new ApiResponse<>(false, "Opération impossible : contrainte d'intégrité violée",
                        request.getDescription(false), new Date()),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiResponse<String>> handleGenericException(
            Exception ex, WebRequest request) {
        log.error("500 INTERNAL SERVER ERROR - {}", ex.getMessage());
        return new ResponseEntity<>(
                new ApiResponse<>(false, "Une erreur interne est survenue", request.getDescription(false), new Date()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}