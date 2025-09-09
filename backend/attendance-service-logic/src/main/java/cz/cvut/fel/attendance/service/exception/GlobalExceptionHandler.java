package cz.cvut.fel.attendance.service.exception;

import cz.fel.cvut.attendance.service.exception.ChildException;
import cz.fel.cvut.attendance.service.exception.SchoolException;
import cz.fel.cvut.attendance.service.exception.TrainingException;
import cz.fel.cvut.attendance.service.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(SchoolException.class)
    public ResponseEntity<Map<String, String>> handleSchoolException(SchoolException ex) {
        return ResponseEntity.status(ex.getStatus()).body(Map.of("status", String.valueOf(ex.getStatus().value()),"message", ex.getMessage()));
    }

    @ExceptionHandler(TrainingException.class)
    public ResponseEntity<Map<String, String>> handleTrainingException(TrainingException ex) {
        return ResponseEntity.status(ex.getStatus()).body(Map.of("status", String.valueOf(ex.getStatus().value()),"message", ex.getMessage()));
    }

    @ExceptionHandler(ChildException.class)
    public ResponseEntity<Map<String, String>> handleChildException(ChildException ex) {
        return ResponseEntity.status(ex.getStatus()).body(Map.of("status", String.valueOf(ex.getStatus().value()),"message", ex.getMessage()));
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<Map<String, String>> handleUserException(UserException ex) {
        return ResponseEntity.status(ex.getStatus()).body(Map.of("status", String.valueOf(ex.getStatus().value()),"message", ex.getMessage()));
    }

    // AUTO
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("Invalid JSON input: {}", ex.getLocalizedMessage());
        return ResponseEntity.badRequest().body(Map.of("message", "Invalid JSON input: " + ex.getLocalizedMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        log.error("An unexpected error occurred: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "An unexpected error occurred: " + ex.getMessage()));
    }
}
