package com.jhon.logistics.domain.service.logistics;

import com.jhon.logistics.application.dto.request.logistics.LandShipmentRequest;
import com.jhon.logistics.application.dto.response.logistics.LandShipmentResponse;

import java.util.List;

public interface LandShipmentService {
    List<LandShipmentResponse> findAll();
    LandShipmentResponse findById(Long id);
    LandShipmentResponse create(LandShipmentRequest req);
    LandShipmentResponse update(Long id, LandShipmentRequest req);
    void delete(Long id);
}
