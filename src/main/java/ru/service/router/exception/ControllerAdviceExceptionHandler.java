package ru.service.router.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ControllerAdviceExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody
    public ResponseEntity<String> handle(final ResourceNotFoundException exception) {
        return ResponseEntity.internalServerError().body(exception.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseBody
    public ResponseEntity<String> handle(final BadRequestException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseBody
    public ResponseEntity<String> handle(final ConflictException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }
}
