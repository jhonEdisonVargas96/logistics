package com.jhon.logistics.infrastructure.adapter.out.persistence.logistics;

import com.jhon.logistics.domain.model.logistics.LandShipment;
import com.jhon.logistics.domain.port.out.repository.logistics.LandShipmentRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class LandShipmentRepositoryImpl implements LandShipmentRepository {

    private final JdbcTemplate jdbc;

    private final RowMapper<LandShipment> rowMapper = (rs, rn) -> LandShipment.builder()
            .id(rs.getLong("id"))
            .clientId(rs.getLong("client_id"))
            .productTypeId(rs.getLong("product_type_id"))
            .warehouseId(rs.getLong("warehouse_id"))
            .quantity(rs.getInt("quantity"))
            .registrationDate(rs.getDate("registration_date").toLocalDate())
            .deliveryDate(rs.getDate("delivery_date").toLocalDate())
            .trackingNumber(rs.getString("tracking_number"))
            .vehiclePlate(rs.getString("vehicle_plate"))
            .basePrice(rs.getBigDecimal("base_price"))
            .finalPrice(rs.getBigDecimal("final_price"))
            .build();

    public LandShipmentRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<LandShipment> findAll() {
        return jdbc.query("SELECT * FROM land_shipment ORDER BY id DESC", rowMapper);
    }

    @Override
    public Optional<LandShipment> findById(Long id) {
        return jdbc.query("SELECT * FROM land_shipment WHERE id = ?",
                rowMapper, id).stream().findFirst();
    }

    @Override
    public boolean existsByTrackingNumber(String trackingNumber) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM land_shipment WHERE tracking_number = ?",
                Integer.class, trackingNumber);
        return count != null && count > 0;
    }

    @Override
    public LandShipment save(LandShipment s) {
        Long id = jdbc.queryForObject("""
            INSERT INTO land_shipment
                (client_id, product_type_id, warehouse_id, quantity,
                 registration_date, delivery_date, tracking_number,
                 vehicle_plate, base_price, final_price)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id
            """, Long.class,
                s.getClientId(), s.getProductTypeId(), s.getWarehouseId(), s.getQuantity(),
                s.getRegistrationDate(), s.getDeliveryDate(), s.getTrackingNumber(),
                s.getVehiclePlate(), s.getBasePrice(), s.getFinalPrice());
        s.setId(id);
        return s;
    }

    @Override
    public void update(LandShipment s) {
        jdbc.update("""
            UPDATE land_shipment SET
                client_id = ?, product_type_id = ?, warehouse_id = ?, quantity = ?,
                delivery_date = ?, tracking_number = ?, vehicle_plate = ?,
                base_price = ?, final_price = ?
            WHERE id = ?
            """,
                s.getClientId(), s.getProductTypeId(), s.getWarehouseId(), s.getQuantity(),
                s.getDeliveryDate(), s.getTrackingNumber(), s.getVehiclePlate(),
                s.getBasePrice(), s.getFinalPrice(), s.getId());
    }

    @Override
    public void deleteById(Long id) {
        jdbc.update("DELETE FROM land_shipment WHERE id = ?", id);
    }
}
