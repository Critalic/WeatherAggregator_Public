package com.example.weatheraggregator.api.advice;

import com.example.weatheraggregator.dto.response.ExceptionResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@RestControllerAdvice
public class GlobalExceptionAdvice {
    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionResponseDTO> handleValidationException(MethodArgumentNotValidException e,
                                                                          WebRequest request) {
        StringBuilder errorMessage = new StringBuilder("Validation error: ");
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errorMessage.append(error.getDefaultMessage()).append("\n");
        }
        return ResponseEntity.badRequest().body(new ExceptionResponseDTO(errorMessage.toString(),
                request.getDescription(false), HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now().toString()));
    }

    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionResponseDTO> handleIllegalArgumentException(IllegalArgumentException e, WebRequest request) {
        String errorMessage = String.format("Exception caused by an illegal argument: %s", e.getMessage());
        return ResponseEntity.badRequest().body(new ExceptionResponseDTO(errorMessage,
                request.getDescription(false), HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now().toString()));
    }

    @ExceptionHandler({DateTimeParseException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionResponseDTO> handleIllegalArgumentException(DateTimeParseException e, WebRequest request) {
        String errorMessage = "Invalid date parameter passed, please check the date parameter";
        return ResponseEntity.badRequest().body(new ExceptionResponseDTO(errorMessage,
                request.getDescription(false), HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now().toString()));
    }
}
