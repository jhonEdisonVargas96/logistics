package com.jhon.logistics.infrastructure.adapter.out.persistence.core;

import com.jhon.logistics.domain.model.core.User;
import com.jhon.logistics.domain.port.out.repository.core.UserRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final JdbcTemplate jdbc;

    private final RowMapper<User> rowMapper = (rs, rn) -> User.builder()
            .id(rs.getLong("id"))
            .username(rs.getString("username"))
            .email(rs.getString("email"))
            .passwordHash(rs.getString("password_hash"))
            .role(rs.getString("role"))
            .status(rs.getString("status"))
            .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
            .build();

    public UserRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<User> findByEmailAndStatus(String email, String status) {
        return jdbc.query(
                "SELECT * FROM app_user WHERE email = ? AND status = ?",
                rowMapper, email, status
        ).stream().findFirst();
    }

    @Override
    public boolean existsByEmail(String email) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM app_user WHERE email = ?",
                Integer.class, email);
        return count != null && count > 0;
    }

    @Override
    public void save(User u) {
        Long id = jdbc.queryForObject("""
            INSERT INTO app_user (username, email, password_hash, role, status, created_at)
            VALUES (?, ?, ?, ?, ?, ?)
            RETURNING id
            """, Long.class,
                u.getUsername(), u.getEmail(), u.getPasswordHash(),
                u.getRole(), u.getStatus(), u.getCreatedAt());
        u.setId(id);
    }
}
