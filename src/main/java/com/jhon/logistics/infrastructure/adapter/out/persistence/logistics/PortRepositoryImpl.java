package com.jhon.logistics.infrastructure.adapter.out.persistence.logistics;

import com.jhon.logistics.domain.model.logistics.Port;
import com.jhon.logistics.domain.port.out.repository.logistics.PortRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PortRepositoryImpl implements PortRepository {

    private final JdbcTemplate jdbc;

    private final RowMapper<Port> rowMapper = (rs, rn) -> Port.builder()
            .id(rs.getLong("id"))
            .name(rs.getString("name"))
            .city(rs.getString("city"))
            .country(rs.getString("country"))
            .portType(rs.getString("port_type"))
            .status(rs.getString("status"))
            .build();

    @Override
    public List<Port> findAllActive() {
        return jdbc.query(
                "SELECT * FROM port WHERE status = 'A' ORDER BY id",
                rowMapper
        );
    }

    @Override
    public Optional<Port> findByIdAndStatus(Long id, String status) {
        return jdbc.query(
                "SELECT * FROM port WHERE id = ? AND status = ?",
                rowMapper, id, status
        ).stream().findFirst();
    }

    @Override
    public Port save(Port p) {
        Long id = jdbc.queryForObject("""
            INSERT INTO port (name, city, country, port_type, status)
            VALUES (?, ?, ?, ?, ?) RETURNING id
            """, Long.class,
                p.getName(), p.getCity(), p.getCountry(), p.getPortType(), p.getStatus());
        p.setId(id);
        return p;
    }

    @Override
    public void update(Port p) {
        jdbc.update("""
            UPDATE port SET name = ?, city = ?, country = ?, port_type = ?
            WHERE id = ?
            """,
                p.getName(), p.getCity(), p.getCountry(), p.getPortType(), p.getId());
    }

    @Override
    public void deleteById(Long id) {
        jdbc.update("UPDATE port SET status = 'I' WHERE id = ?", id);
    }
}