package com.jhon.logistics.infrastructure.adapter.in.web.logistics;

import com.jhon.logistics.application.dto.request.logistics.PortRequest;
import com.jhon.logistics.application.dto.response.global.ApiResponse;
import com.jhon.logistics.application.dto.response.logistics.PortResponse;
import com.jhon.logistics.domain.service.logistics.PortService;
import com.jhon.logistics.infrastructure.adapter.in.web.BaseController;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.base-path}/${api.version}/ports")
@RequiredArgsConstructor
@Validated
public class PortController extends BaseController {

    private final PortService portService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PortResponse>>> findAll() {
        return okList(portService.findAll(), "Ports retrieved successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PortResponse>> findById(@PathVariable Long id) {
        return ok(portService.findById(id), "Port retrieved successfully");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PortResponse>> create(
            @Valid @RequestBody PortRequest request) {
        return created(portService.create(request), "Port created successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PortResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody PortRequest request) {
        return ok(portService.update(id, request), "Port updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        portService.delete(id);
        return noContent("Port deleted successfully");
    }
}