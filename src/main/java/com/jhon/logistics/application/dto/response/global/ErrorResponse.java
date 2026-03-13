package com.jhon.logistics.application.dto.response.global;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private int status;
    private String message;
    private List<String> details;
    private Map<String, String> fieldErrors;
    private String path;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    // Constructor completo con List<String>
    public ErrorResponse(int status, String message, List<String> details, String path, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.details = details;
        this.path = path;
        this.timestamp = timestamp;
    }

    // Constructor para errores de validación con Map
    public ErrorResponse(int status, String message, Map<String, String> fieldErrors, String path, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.fieldErrors = fieldErrors;
        this.path = path;
        this.timestamp = timestamp;
    }
}