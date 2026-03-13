package com.jhon.logistics.domain.service.logistics;

import com.jhon.logistics.application.dto.request.logistics.WarehouseRequest;
import com.jhon.logistics.application.dto.response.logistics.WarehouseResponse;

import java.util.List;

public interface WarehouseService {
    List<WarehouseResponse> findAll();
    WarehouseResponse findById(Long id);
    WarehouseResponse create(WarehouseRequest req);
    WarehouseResponse update(Long id, WarehouseRequest req);
    void delete(Long id);
}
