package com.jhon.logistics.domain.service.core.impl;

import com.jhon.logistics.application.dto.request.core.LoginRequest;
import com.jhon.logistics.application.dto.request.core.RegisterRequest;
import com.jhon.logistics.application.dto.response.global.AuthResponse;
import com.jhon.logistics.domain.model.core.User;
import com.jhon.logistics.domain.port.out.repository.core.UserRepository;
import com.jhon.logistics.domain.service.core.AuthService;
import com.jhon.logistics.infrastructure.config.security.jwt.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }
        User user = User.builder()
                .username(req.username())
                .email(req.email())
                .passwordHash(passwordEncoder.encode(req.password()))
                .role("USER")
                .status("A")
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);
        return new AuthResponse(jwtService.generateToken(user.getEmail(), user.getRole()), user.getRole());
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmailAndStatus(req.email(), "A")
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        return new AuthResponse(jwtService.generateToken(user.getEmail(), user.getRole()), user.getRole());
    }
}
