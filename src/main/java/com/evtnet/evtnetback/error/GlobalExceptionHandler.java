package com.evtnet.evtnetback.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpErrorException.class)
    public ResponseEntity<Map<String,Object>> handleHttpError(HttpErrorException ex) {
        return ResponseEntity.status(400).body(Map.of(
                "code", ex.getCode(),
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handleGeneric(Exception ex) {
        return ResponseEntity.status(500).body(Map.of(
                "code", -1,
                "message", "Error interno del servidor"
        ));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String,Object>> handleNotFound(NoResourceFoundException ex) {
        return ResponseEntity.status(404).body(Map.of(
                "code", 404,
                "message", "Endpoint no encontrado"
        ));
    }
}
