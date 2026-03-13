package com.jhon.logistics.infrastructure.config.security.jwt;

import com.jhon.logistics.domain.model.core.User;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class UserAuthenticationToken implements Authentication, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Getter
    private final String email;

    @Getter private final String role;

    @Getter @Setter
    private transient User user;

    private boolean authenticated = true;

    public UserAuthenticationToken(String email, String role, User user) {
        this.email = email;
        this.role  = role;
        this.user  = user;
    }

    @Override public String getName()        { return email; }
    @Override public Object getPrincipal()   { return user; }
    @Override public Object getCredentials() { return null; }
    @Override public Object getDetails()     { return Map.of("email", email, "role", role); }
    @Override
    public @NonNull Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }
    @Override public boolean isAuthenticated()        { return authenticated; }
    @Override public void setAuthenticated(boolean v) { this.authenticated = v; }
}
