package com.jhon.logistics.infrastructure.adapter.out.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jhon.logistics.application.dto.response.global.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper;

    public RestAuthenticationEntryPoint(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         @NonNull AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        ErrorResponse body = new ErrorResponse(
                401,
                "Authentication required",
                List.of("Debe ingresar el token de autenticación"),
                request.getRequestURI(),
                LocalDateTime.now()
        );

        mapper.writeValue(response.getOutputStream(), body);
    }
}