package com.jhon.logistics.infrastructure.adapter.in.web.logistics;

import com.jhon.logistics.application.dto.request.logistics.WarehouseRequest;
import com.jhon.logistics.application.dto.response.global.ApiResponse;
import com.jhon.logistics.application.dto.response.logistics.WarehouseResponse;
import com.jhon.logistics.domain.service.logistics.WarehouseService;
import com.jhon.logistics.infrastructure.adapter.in.web.BaseController;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.base-path}/${api.version}/warehouses")
@RequiredArgsConstructor
@Validated
public class WarehouseController extends BaseController {

    private final WarehouseService warehouseService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<WarehouseResponse>>> findAll() {
        return okList(warehouseService.findAll(), "Warehouses retrieved successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WarehouseResponse>> findById(@PathVariable Long id) {
        return ok(warehouseService.findById(id), "Warehouse retrieved successfully");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WarehouseResponse>> create(
            @Valid @RequestBody WarehouseRequest request) {
        return created(warehouseService.create(request), "Warehouse created successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WarehouseResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody WarehouseRequest request) {
        return ok(warehouseService.update(id, request), "Warehouse updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        warehouseService.delete(id);
        return noContent("Warehouse deleted successfully");
    }
}