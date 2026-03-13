package com.jhon.logistics.infrastructure.adapter.out.exception;

import com.jhon.logistics.application.dto.response.global.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String UNEXPECTED_ERROR = "Error inesperado";

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpServletRequest request) {

        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Malformed JSON request",
                List.of("Invalid request body"),
                request.getRequestURI(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // 422 - Bean Validation en @RequestBody (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> Objects.requireNonNullElse(fe.getDefaultMessage(), "Invalid value"),
                        (existing, replacement) -> existing
                ));

        ErrorResponse body = new ErrorResponse(
                HttpStatus.UNPROCESSABLE_CONTENT.value(),
                "Validation failed",
                fieldErrors,
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(body);
    }

    // 400 - Constraint violations (ej. @Validated en params/path)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {

        List<String> details = ex.getConstraintViolations()
                .stream()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .toList();

        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                details,
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // 400 - Errores de lógica de negocio (BusinessException)
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex,
            HttpServletRequest request) {

        HttpStatus status = ex.getHttpStatus();

        log.warn("Business exception [{}]: {}", status, ex.getMessage());

        ErrorResponse body = new ErrorResponse(
                status.value(),
                "Business rule violation",
                List.of(ex.getMessage()),
                request.getRequestURI(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(status).body(body);
    }

    // 400 - validaciones manuales que lanzan IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid argument",
                List.of(ex.getMessage() != null ? ex.getMessage() : "Argumento inválido"),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // 409 - Conflictos de datos únicos (ejemplo: NIT duplicado)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex,
                                                                      HttpServletRequest request) {
        log.error("Data integrity violation", ex);

        String message = "Data integrity violation";
        ex.getMostSpecificCause();
        if (ex.getMostSpecificCause().getMessage() != null && ex.getMostSpecificCause().getMessage().toLowerCase().contains("unique")) {
            message = "The data already exists in the system";
        }

        ErrorResponse body = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                message,
                List.of("Please check for duplicate values"),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }


    // Maneja ResponseStatusException respetando su status y mensaje
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(
            ResponseStatusException ex,
            HttpServletRequest request
    ) {
        String reason = ex.getReason() != null ? ex.getReason() : UNEXPECTED_ERROR;

        ErrorResponse body = new ErrorResponse(
                ex.getStatusCode().value(),
                reason,
                List.of(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(ex.getStatusCode()).body(body);
    }

    // 404 - handler no encontrado (endpoints)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(NoHandlerFoundException ex, HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                List.of("Recurso no encontrado: " + ex.getRequestURL()),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        log.info("NoHandlerFound: {} -> {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // 404 - recurso estático no encontrado (ejemplo: ResourceHttpRequestHandler)
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException ex, HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                List.of("Recurso estático no encontrado: " + ex.getMessage()),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        log.info("NoResourceFound: {} -> {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // 500 - fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex, HttpServletRequest request) {
        log.error(UNEXPECTED_ERROR, ex);
        ErrorResponse body = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                UNEXPECTED_ERROR,
                List.of("Please contact system administrator"),
                request.getRequestURI(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}