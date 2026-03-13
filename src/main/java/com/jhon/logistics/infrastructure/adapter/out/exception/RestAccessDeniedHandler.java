package com.jhon.logistics.infrastructure.adapter.out.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jhon.logistics.application.dto.response.global.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;


@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper mapper;

    public RestAccessDeniedHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       @NonNull AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");

        ErrorResponse body = new ErrorResponse(
                403,
                "Access denied",
                List.of("No hay permisos para este recurso."),
                request.getRequestURI(),
                LocalDateTime.now()
        );

        mapper.writeValue(response.getOutputStream(), body);
    }
}