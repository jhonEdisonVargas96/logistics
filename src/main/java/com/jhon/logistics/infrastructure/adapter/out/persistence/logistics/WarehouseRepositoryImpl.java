package com.jhon.logistics.infrastructure.adapter.out.persistence.logistics;

import com.jhon.logistics.domain.model.logistics.Warehouse;
import com.jhon.logistics.domain.port.out.repository.logistics.WarehouseRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class WarehouseRepositoryImpl implements WarehouseRepository {

    private final JdbcTemplate jdbc;

    private final RowMapper<Warehouse> rowMapper = (rs, rn) -> Warehouse.builder()
            .id(rs.getLong("id"))
            .name(rs.getString("name"))
            .address(rs.getString("address"))
            .city(rs.getString("city"))
            .status(rs.getString("status"))
            .build();

    public WarehouseRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Warehouse> findAllActive() {
        return jdbc.query(
                "SELECT * FROM warehouse WHERE status = 'A' ORDER BY id",
                rowMapper
        );
    }

    @Override
    public Optional<Warehouse> findByIdAndStatus(Long id, String status) {
        return jdbc.query(
                "SELECT * FROM warehouse WHERE id = ? AND status = ?",
                rowMapper, id, status
        ).stream().findFirst();
    }

    @Override
    public Warehouse save(Warehouse w) {
        Long id = jdbc.queryForObject("""
            INSERT INTO warehouse (name, address, city, status)
            VALUES (?, ?, ?, ?) RETURNING id
            """, Long.class,
                w.getName(), w.getAddress(), w.getCity(), w.getStatus());
        w.setId(id);
        return w;
    }

    @Override
    public void update(Warehouse w) {
        jdbc.update("""
            UPDATE warehouse SET name = ?, address = ?, city = ?
            WHERE id = ?
            """,
                w.getName(), w.getAddress(), w.getCity(), w.getId());
    }

    @Override
    public void deleteById(Long id) {
        jdbc.update("UPDATE warehouse SET status = 'I' WHERE id = ?", id);
    }
}
