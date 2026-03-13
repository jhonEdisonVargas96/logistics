package com.jhon.logistics.domain.service.logistics;

import com.jhon.logistics.application.dto.request.logistics.ProductTypeRequest;
import com.jhon.logistics.application.dto.response.logistics.ProductTypeResponse;

import java.util.List;

public interface ProductTypeService {
    List<ProductTypeResponse> findAll();
    ProductTypeResponse findById(Long id);
    ProductTypeResponse create(ProductTypeRequest req);
    ProductTypeResponse update(Long id, ProductTypeRequest req);
    void delete(Long id);
}
