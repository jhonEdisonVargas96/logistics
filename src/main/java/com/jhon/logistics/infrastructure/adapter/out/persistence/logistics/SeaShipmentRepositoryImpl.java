package com.jhon.logistics.infrastructure.adapter.out.persistence.logistics;

import com.jhon.logistics.domain.model.logistics.SeaShipment;
import com.jhon.logistics.domain.port.out.repository.logistics.SeaShipmentRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SeaShipmentRepositoryImpl implements SeaShipmentRepository {

    private final JdbcTemplate jdbc;

    private final RowMapper<SeaShipment> rowMapper = (rs, rn) -> SeaShipment.builder()
            .id(rs.getLong("id"))
            .clientId(rs.getLong("client_id"))
            .productTypeId(rs.getLong("product_type_id"))
            .portId(rs.getLong("port_id"))
            .quantity(rs.getInt("quantity"))
            .registrationDate(rs.getDate("registration_date").toLocalDate())
            .deliveryDate(rs.getDate("delivery_date").toLocalDate())
            .trackingNumber(rs.getString("tracking_number"))
            .fleetNumber(rs.getString("fleet_number"))
            .basePrice(rs.getBigDecimal("base_price"))
            .finalPrice(rs.getBigDecimal("final_price"))
            .build();

    public SeaShipmentRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<SeaShipment> findAll() {
        return jdbc.query(
                "SELECT * FROM sea_shipment ORDER BY id DESC",
                rowMapper
        );
    }

    @Override
    public Optional<SeaShipment> findById(Long id) {
        return jdbc.query(
                "SELECT * FROM sea_shipment WHERE id = ?",
                rowMapper, id
        ).stream().findFirst();
    }

    @Override
    public boolean existsByTrackingNumber(String trackingNumber) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM sea_shipment WHERE tracking_number = ?",
                Integer.class, trackingNumber);
        return count != null && count > 0;
    }

    @Override
    public SeaShipment save(SeaShipment s) {
        Long id = jdbc.queryForObject("""
            INSERT INTO sea_shipment
                (client_id, product_type_id, port_id, quantity,
                 registration_date, delivery_date, tracking_number,
                 fleet_number, base_price, final_price)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id
            """, Long.class,
                s.getClientId(), s.getProductTypeId(), s.getPortId(), s.getQuantity(),
                s.getRegistrationDate(), s.getDeliveryDate(), s.getTrackingNumber(),
                s.getFleetNumber(), s.getBasePrice(), s.getFinalPrice());
        s.setId(id);
        return s;
    }

    @Override
    public void update(SeaShipment s) {
        jdbc.update("""
            UPDATE sea_shipment SET
                client_id = ?, product_type_id = ?, port_id = ?, quantity = ?,
                delivery_date = ?, tracking_number = ?, fleet_number = ?,
                base_price = ?, final_price = ?
            WHERE id = ?
            """,
                s.getClientId(), s.getProductTypeId(), s.getPortId(), s.getQuantity(),
                s.getDeliveryDate(), s.getTrackingNumber(), s.getFleetNumber(),
                s.getBasePrice(), s.getFinalPrice(), s.getId());
    }

    @Override
    public void deleteById(Long id) {
        jdbc.update("DELETE FROM sea_shipment WHERE id = ?", id);
    }
}