package com.jhon.logistics.infrastructure.adapter.in.web.logistics;

import com.jhon.logistics.application.dto.request.logistics.SeaShipmentRequest;
import com.jhon.logistics.application.dto.response.global.ApiResponse;
import com.jhon.logistics.application.dto.response.logistics.SeaShipmentResponse;
import com.jhon.logistics.domain.service.logistics.SeaShipmentService;
import com.jhon.logistics.infrastructure.adapter.in.web.BaseController;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.base-path}/${api.version}/sea-shipments")
@RequiredArgsConstructor
@Validated
public class SeaShipmentController extends BaseController {

    private final SeaShipmentService seaShipmentService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SeaShipmentResponse>>> findAll() {
        return okList(seaShipmentService.findAll(), "Sea shipments retrieved successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SeaShipmentResponse>> findById(@PathVariable Long id) {
        return ok(seaShipmentService.findById(id), "Sea shipment retrieved successfully");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SeaShipmentResponse>> create(
            @Valid @RequestBody SeaShipmentRequest request) {
        return created(seaShipmentService.create(request), "Sea shipment created successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SeaShipmentResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody SeaShipmentRequest request) {
        return ok(seaShipmentService.update(id, request), "Sea shipment updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        seaShipmentService.delete(id);
        return noContent("Sea shipment deleted successfully");
    }
}