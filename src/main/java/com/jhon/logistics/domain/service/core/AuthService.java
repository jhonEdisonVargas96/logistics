package com.jhon.logistics.domain.service.core;

import com.jhon.logistics.application.dto.request.core.LoginRequest;
import com.jhon.logistics.application.dto.request.core.RegisterRequest;
import com.jhon.logistics.application.dto.response.global.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest req);
    AuthResponse login(LoginRequest req);
}
