package com.jhon.logistics.infrastructure.config.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private static final long EXPIRATION_MS = Duration.ofHours(2).toMillis();

    /**
     * Generates a signed JWT token for the given email and role.
     */
    public String generateToken(String email, String role) {
        Date now    = new Date();
        Date expiry = new Date(now.getTime() + EXPIRATION_MS);
        SecretKey key = buildKey();

        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    /**
     * Extracts the email (subject) from the token.
     */
    public String getEmailFromToken(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Extracts the role claim from the token.
     */
    public String getRoleFromToken(String token) {
        return getClaims(token).get("role", String.class);
    }

    /**
     * Returns true if the token is well-formed, signed correctly, and not expired.
     */
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(buildKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey buildKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
}
