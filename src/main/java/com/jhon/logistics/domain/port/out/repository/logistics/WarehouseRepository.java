package com.jhon.logistics.domain.port.out.repository.logistics;

import com.jhon.logistics.domain.model.logistics.Warehouse;

import java.util.List;
import java.util.Optional;

public interface WarehouseRepository {
    List<Warehouse> findAllActive();
    Optional<Warehouse> findByIdAndStatus(Long id, String status);
    Warehouse save(Warehouse warehouse);
    void update(Warehouse warehouse);
    void deleteById(Long id);
}
