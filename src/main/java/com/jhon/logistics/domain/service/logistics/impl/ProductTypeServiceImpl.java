package com.jhon.logistics.domain.service.logistics.impl;

import com.jhon.logistics.application.dto.request.logistics.ProductTypeRequest;
import com.jhon.logistics.application.dto.response.logistics.ProductTypeResponse;
import com.jhon.logistics.domain.model.logistics.ProductType;
import com.jhon.logistics.domain.port.out.repository.logistics.ProductTypeRepository;
import com.jhon.logistics.domain.service.logistics.ProductTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductTypeServiceImpl implements ProductTypeService {

    private static final String NOT_FOUND = "Product type not found";

    private final ProductTypeRepository productTypeRepository;

    public ProductTypeServiceImpl(ProductTypeRepository productTypeRepository) {
        this.productTypeRepository = productTypeRepository;
    }

    public List<ProductTypeResponse> findAll() {
        return productTypeRepository.findAllActive().stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductTypeResponse findById(Long id) {
        return productTypeRepository.findByIdAndStatus(id, "A")
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, NOT_FOUND));
    }

    public ProductTypeResponse create(ProductTypeRequest req) {
        ProductType productType = ProductType.builder()
                .name(req.name())
                .description(req.description())
                .status("A")
                .build();
        return toResponse(productTypeRepository.save(productType));
    }

    public ProductTypeResponse update(Long id, ProductTypeRequest req) {
        ProductType productType = productTypeRepository.findByIdAndStatus(id, "A")
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, NOT_FOUND));

        productType.setName(req.name());
        productType.setDescription(req.description());
        productTypeRepository.update(productType);

        return toResponse(productType);
    }

    public void delete(Long id) {
        productTypeRepository.findByIdAndStatus(id, "A")
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, NOT_FOUND));
        productTypeRepository.deleteById(id);
    }

    private ProductTypeResponse toResponse(ProductType p) {
        return new ProductTypeResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getStatus()
        );
    }
}