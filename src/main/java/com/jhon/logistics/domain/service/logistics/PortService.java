package com.jhon.logistics.domain.service.logistics;

import com.jhon.logistics.application.dto.request.logistics.PortRequest;
import com.jhon.logistics.application.dto.response.logistics.PortResponse;

import java.util.List;

public interface PortService {
    List<PortResponse> findAll();
    PortResponse findById(Long id);
    PortResponse create(PortRequest req);
    PortResponse update(Long id, PortRequest req);
    void delete(Long id);
}
