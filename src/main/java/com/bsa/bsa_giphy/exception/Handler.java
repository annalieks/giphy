package com.bsa.bsa_giphy.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.Map;

@ControllerAdvice
public final class Handler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(FileProcessingException.class)
    public ResponseEntity<Object> handleApplicationIOException(FileProcessingException e) {
        return ResponseEntity
                .status(500)
                .body(
                        Map.of("Error appeared",
                                e.getMessage() == null
                                       ? "Could not process file"
                                       : e.getMessage()
                        )
                );
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<Object> handleNotFoundData(DataNotFoundException e) {
        return ResponseEntity
                .notFound().build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleApplicationBusinessLogicException(ConstraintViolationException e) {
        return ResponseEntity
                .unprocessableEntity()
                .body(
                        Map.of(
                                "Error appeared.",
                                e.getMessage() == null
                                        ? "Invalid request parameters"
                                        : e.getMessage()
                        )
                );
    }
}
