package com.jhon.logistics.infrastructure.adapter.in.web.logistics;

import com.jhon.logistics.application.dto.request.logistics.ClientRequest;
import com.jhon.logistics.application.dto.response.global.ApiResponse;
import com.jhon.logistics.application.dto.response.logistics.ClientResponse;
import com.jhon.logistics.domain.service.logistics.ClientService;
import com.jhon.logistics.infrastructure.adapter.in.web.BaseController;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.base-path}/${api.version}/clients")
@RequiredArgsConstructor
@Validated
public class ClientController extends BaseController {

    private final ClientService clientService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClientResponse>>> findAll() {
        return okList(clientService.findAll(), "Clients retrieved successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClientResponse>> findById(@PathVariable Long id) {
        return ok(clientService.findById(id), "Client retrieved successfully");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ClientResponse>> create(
            @Valid @RequestBody ClientRequest request) {
        return created(clientService.create(request), "Client created successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClientResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ClientRequest request) {
        return ok(clientService.update(id, request), "Client updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        clientService.delete(id);
        return noContent("Client deleted successfully");
    }
}