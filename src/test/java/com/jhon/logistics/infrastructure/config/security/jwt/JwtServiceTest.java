package com.jhon.logistics.infrastructure.config.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

@DisplayName("JwtService")
class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret",
                "my-super-secret-key-of-at-least-256-bits-for-hs256");
    }

    @Test
    @DisplayName("generates valid token and extracts email")
    void generatesTokenAndExtractsEmail() {
        String token = jwtService.generateToken("user@mail.com", "USER");

        assertThat(jwtService.getEmailFromToken(token)).isEqualTo("user@mail.com");
    }

    @Test
    @DisplayName("extracts role from token")
    void extractsRoleFromToken() {
        String token = jwtService.generateToken("user@mail.com", "ADMIN");

        assertThat(jwtService.getRoleFromToken(token)).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("validates a well-formed token")
    void validatesWellFormedToken() {
        String token = jwtService.generateToken("user@mail.com", "USER");

        assertThat(jwtService.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("rejects a malformed token")
    void rejectsMalformedToken() {
        assertThat(jwtService.validateToken("not.a.valid.token")).isFalse();
    }

    @Test
    @DisplayName("rejects empty token")
    void rejectsEmptyToken() {
        assertThat(jwtService.validateToken("")).isFalse();
    }
}