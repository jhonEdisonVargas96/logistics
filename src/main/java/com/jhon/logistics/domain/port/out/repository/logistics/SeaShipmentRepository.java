package com.jhon.logistics.domain.port.out.repository.logistics;

import com.jhon.logistics.domain.model.logistics.SeaShipment;

import java.util.List;
import java.util.Optional;

public interface SeaShipmentRepository {
    List<SeaShipment> findAll();
    Optional<SeaShipment> findById(Long id);
    boolean existsByTrackingNumber(String trackingNumber);
    SeaShipment save(SeaShipment shipment);
    void update(SeaShipment shipment);
    void deleteById(Long id);
}
