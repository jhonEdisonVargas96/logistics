package com.jhon.logistics.domain.port.out.repository.logistics;

import com.jhon.logistics.domain.model.logistics.ProductType;

import java.util.List;
import java.util.Optional;

public interface ProductTypeRepository {
    List<ProductType> findAllActive();
    Optional<ProductType> findByIdAndStatus(Long id, String status);
    ProductType save(ProductType productType);
    void update(ProductType productType);
    void deleteById(Long id);
}
