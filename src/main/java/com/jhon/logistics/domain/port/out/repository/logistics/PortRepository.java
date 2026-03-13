package com.jhon.logistics.domain.port.out.repository.logistics;

import com.jhon.logistics.domain.model.logistics.Port;

import java.util.List;
import java.util.Optional;

public interface PortRepository {
    List<Port> findAllActive();
    Optional<Port> findByIdAndStatus(Long id, String status);
    Port save(Port port);
    void update(Port port);
    void deleteById(Long id);
}
