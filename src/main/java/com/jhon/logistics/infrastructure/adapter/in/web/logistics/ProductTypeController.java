package com.jhon.logistics.infrastructure.adapter.in.web.logistics;

import com.jhon.logistics.application.dto.request.logistics.ProductTypeRequest;
import com.jhon.logistics.application.dto.response.global.ApiResponse;
import com.jhon.logistics.application.dto.response.logistics.ProductTypeResponse;
import com.jhon.logistics.domain.service.logistics.ProductTypeService;
import com.jhon.logistics.infrastructure.adapter.in.web.BaseController;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.base-path}/${api.version}/product-types")
@RequiredArgsConstructor
@Validated
public class ProductTypeController extends BaseController {

    private final ProductTypeService productTypeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductTypeResponse>>> findAll() {
        return okList(productTypeService.findAll(), "Product types retrieved successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductTypeResponse>> findById(@PathVariable Long id) {
        return ok(productTypeService.findById(id), "Product type retrieved successfully");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductTypeResponse>> create(
            @Valid @RequestBody ProductTypeRequest request) {
        return created(productTypeService.create(request), "Product type created successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductTypeResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductTypeRequest request) {
        return ok(productTypeService.update(id, request), "Product type updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productTypeService.delete(id);
        return noContent("Product type deleted successfully");
    }
}