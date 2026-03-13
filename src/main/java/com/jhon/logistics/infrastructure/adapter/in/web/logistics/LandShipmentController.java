package com.jhon.logistics.infrastructure.adapter.in.web.logistics;

import com.jhon.logistics.application.dto.request.logistics.LandShipmentRequest;
import com.jhon.logistics.application.dto.response.global.ApiResponse;
import com.jhon.logistics.application.dto.response.logistics.LandShipmentResponse;
import com.jhon.logistics.domain.service.logistics.LandShipmentService;
import com.jhon.logistics.infrastructure.adapter.in.web.BaseController;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.base-path}/${api.version}/land-shipments")
@RequiredArgsConstructor
@Validated
public class LandShipmentController extends BaseController {

    private final LandShipmentService landShipmentService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<LandShipmentResponse>>> findAll() {
        return okList(landShipmentService.findAll(), "Land shipments retrieved successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LandShipmentResponse>> findById(@PathVariable Long id) {
        return ok(landShipmentService.findById(id), "Land shipment retrieved successfully");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LandShipmentResponse>> create(
            @Valid @RequestBody LandShipmentRequest request) {
        return created(landShipmentService.create(request), "Land shipment created successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LandShipmentResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody LandShipmentRequest request) {
        return ok(landShipmentService.update(id, request), "Land shipment updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        landShipmentService.delete(id);
        return noContent("Land shipment deleted successfully");
    }
}