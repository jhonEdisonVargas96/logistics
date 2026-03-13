package com.jhon.logistics.infrastructure.adapter.out.persistence.logistics;

import com.jhon.logistics.domain.model.logistics.ProductType;
import com.jhon.logistics.domain.port.out.repository.logistics.ProductTypeRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductTypeRepositoryImpl implements ProductTypeRepository {

    private final JdbcTemplate jdbc;

    private final RowMapper<ProductType> rowMapper = (rs, rn) -> ProductType.builder()
            .id(rs.getLong("id"))
            .name(rs.getString("name"))
            .description(rs.getString("description"))
            .status(rs.getString("status"))
            .build();

    public ProductTypeRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<ProductType> findAllActive() {
        return jdbc.query(
                "SELECT * FROM product_type WHERE status = 'A' ORDER BY id",
                rowMapper
        );
    }

    @Override
    public Optional<ProductType> findByIdAndStatus(Long id, String status) {
        return jdbc.query(
                "SELECT * FROM product_type WHERE id = ? AND status = ?",
                rowMapper, id, status
        ).stream().findFirst();
    }

    @Override
    public ProductType save(ProductType p) {
        Long id = jdbc.queryForObject("""
            INSERT INTO product_type (name, description, status)
            VALUES (?, ?, ?) RETURNING id
            """, Long.class,
                p.getName(), p.getDescription(), p.getStatus());
        p.setId(id);
        return p;
    }

    @Override
    public void update(ProductType p) {
        jdbc.update("""
            UPDATE product_type SET name = ?, description = ?
            WHERE id = ?
            """,
                p.getName(), p.getDescription(), p.getId());
    }

    @Override
    public void deleteById(Long id) {
        jdbc.update("UPDATE product_type SET status = 'I' WHERE id = ?", id);
    }
}