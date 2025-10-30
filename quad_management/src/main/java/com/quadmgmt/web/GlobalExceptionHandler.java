
package com.quadmgmt.web;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.OffsetDateTime;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Object> problem(HttpStatus status, String code, String message, String path) {
        return ResponseEntity.status(status).body(Map.of(
                "timestamp", OffsetDateTime.now().toString(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "code", code,
                "message", message,
                "path", path
        ));
    }

    // Option A: IllegalArgumentException -> 400 Bad Request
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {
        return problem(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage(), "");
    }

    // Conflicts (e.g., duplicates signaled by service with IllegalStateException)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Object> handleIllegalState(IllegalStateException ex) {
        return problem(HttpStatus.CONFLICT, "CONFLICT", ex.getMessage(), "");
    }

    // DB-level unique constraint violations
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleConstraint(DataIntegrityViolationException ex) {
        return problem(HttpStatus.CONFLICT, "CONSTRAINT_VIOLATION", "Duplicate or invalid data", "");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex) {
        return problem(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", ex.getMessage(), "");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneric(Exception ex) {
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", ex.getMessage(), "");
    }
}
