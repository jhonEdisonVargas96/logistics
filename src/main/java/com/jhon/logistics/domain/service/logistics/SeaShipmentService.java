package com.jhon.logistics.domain.service.logistics;

import com.jhon.logistics.application.dto.request.logistics.SeaShipmentRequest;
import com.jhon.logistics.application.dto.response.logistics.SeaShipmentResponse;

import java.util.List;

public interface SeaShipmentService {
    List<SeaShipmentResponse> findAll();
    SeaShipmentResponse findById(Long id);
    SeaShipmentResponse create(SeaShipmentRequest req);
    SeaShipmentResponse update(Long id, SeaShipmentRequest req);
    void delete(Long id);
}
