package com.jhon.logistics.infrastructure.adapter.out.persistence.logistics;

import com.jhon.logistics.domain.model.logistics.Client;
import com.jhon.logistics.domain.port.out.repository.logistics.ClientRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class ClientRepositoryImpl implements ClientRepository {

    private final JdbcTemplate jdbc;

    private final RowMapper<Client> rowMapper = (rs, rn) -> Client.builder()
            .id(rs.getLong("id"))
            .name(rs.getString("name"))
            .email(rs.getString("email"))
            .phone(rs.getString("phone"))
            .status(rs.getString("status"))
            .build();

    public ClientRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Client> findAllActive() {
        return jdbc.query(
                "SELECT * FROM client WHERE status = 'A'", rowMapper);
    }

    @Override
    public Optional<Client> findByIdAndStatus(Long id, String status) {
        return jdbc.query("SELECT * FROM client WHERE id = ? AND status = ?",
                rowMapper, id, status).stream().findFirst();
    }

    @Override
    public boolean existsByEmail(String email) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM client WHERE email = ?", Integer.class, email);
        return count != null && count > 0;
    }

    @Override
    public Client save(Client c) {
        Long id = jdbc.queryForObject("""
            INSERT INTO client (name, email, phone, status)
            VALUES (?, ?, ?, ?) RETURNING id
            """, Long.class, c.getName(), c.getEmail(), c.getPhone(), c.getStatus());
        c.setId(id);
        return c;
    }

    @Override
    public void update(Client c) {
        jdbc.update("UPDATE client SET name = ?, email = ?, phone = ? WHERE id = ?",
                c.getName(), c.getEmail(), c.getPhone(), c.getId());
    }

    @Override
    public void deleteById(Long id) {
        jdbc.update("UPDATE client SET status = 'I' WHERE id = ?", id);
    }
}