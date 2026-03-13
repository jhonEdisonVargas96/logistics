package com.jhon.logistics.domain.service.logistics.impl;

import com.jhon.logistics.application.dto.request.logistics.WarehouseRequest;
import com.jhon.logistics.application.dto.response.logistics.WarehouseResponse;
import com.jhon.logistics.domain.model.logistics.Warehouse;
import com.jhon.logistics.domain.port.out.repository.logistics.WarehouseRepository;
import com.jhon.logistics.domain.service.logistics.WarehouseService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class WarehouseServiceImpl implements WarehouseService {

    private static final String NOT_FOUND = "Warehouse not found";

    private final WarehouseRepository warehouseRepository;

    public WarehouseServiceImpl(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    public List<WarehouseResponse> findAll() {
        return warehouseRepository.findAllActive().stream()
                .map(this::toResponse)
                .toList();
    }

    public WarehouseResponse findById(Long id) {
        return warehouseRepository.findByIdAndStatus(id, "A")
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, NOT_FOUND));
    }

    public WarehouseResponse create(WarehouseRequest req) {
        Warehouse warehouse = Warehouse.builder()
                .name(req.name())
                .address(req.address())
                .city(req.city())
                .status("A")
                .build();
        return toResponse(warehouseRepository.save(warehouse));
    }

    public WarehouseResponse update(Long id, WarehouseRequest req) {
        Warehouse warehouse = warehouseRepository.findByIdAndStatus(id, "A")
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, NOT_FOUND));

        warehouse.setName(req.name());
        warehouse.setAddress(req.address());
        warehouse.setCity(req.city());
        warehouseRepository.update(warehouse);

        return toResponse(warehouse);
    }

    public void delete(Long id) {
        warehouseRepository.findByIdAndStatus(id, "A")
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, NOT_FOUND));
        warehouseRepository.deleteById(id);
    }

    private WarehouseResponse toResponse(Warehouse w) {
        return new WarehouseResponse(
                w.getId(),
                w.getName(),
                w.getAddress(),
                w.getCity(),
                w.getStatus()
        );
    }
}