package com.jhon.logistics.domain.port.out.repository.logistics;

import com.jhon.logistics.domain.model.logistics.LandShipment;

import java.util.List;
import java.util.Optional;

public interface LandShipmentRepository {
    List<LandShipment> findAll();
    Optional<LandShipment> findById(Long id);
    boolean existsByTrackingNumber(String trackingNumber);
    LandShipment save(LandShipment shipment);
    void update(LandShipment shipment);
    void deleteById(Long id);
}