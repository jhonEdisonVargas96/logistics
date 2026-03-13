package com.jhon.logistics.infrastructure.config.security.jwt;

import com.jhon.logistics.domain.port.out.repository.core.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService     jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest  request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain         chain
    ) throws ServletException, IOException {

        extractToken(request).ifPresent(token -> {
            if (jwtService.validateToken(token)) {
                String email = jwtService.getEmailFromToken(token);
                String role  = jwtService.getRoleFromToken(token);

                userRepository.findByEmailAndStatus(email, "A")
                        .ifPresent(user -> {
                            UserAuthenticationToken auth =
                                    new UserAuthenticationToken(email, role, user);
                            SecurityContextHolder.getContext().setAuthentication(auth);
                        });
            }
        });

        chain.doFilter(request, response);
    }

    /**
     * Extracts the Bearer token from the Authorization header.
     */
    private java.util.Optional<String> extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return java.util.Optional.of(header.substring(7));
        }
        return java.util.Optional.empty();
    }
}
