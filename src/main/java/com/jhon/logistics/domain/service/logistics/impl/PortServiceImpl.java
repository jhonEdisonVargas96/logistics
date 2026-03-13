package com.jhon.logistics.domain.service.logistics.impl;

import com.jhon.logistics.application.dto.request.logistics.PortRequest;
import com.jhon.logistics.application.dto.response.logistics.PortResponse;
import com.jhon.logistics.domain.model.logistics.Port;
import com.jhon.logistics.domain.port.out.repository.logistics.PortRepository;
import com.jhon.logistics.domain.service.logistics.PortService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PortServiceImpl implements PortService {

    private static final String NOT_FOUND = "Port not found";

    private final PortRepository portRepository;

    public PortServiceImpl(PortRepository portRepository) {
        this.portRepository = portRepository;
    }

    public List<PortResponse> findAll() {
        return portRepository.findAllActive().stream()
                .map(this::toResponse)
                .toList();
    }

    public PortResponse findById(Long id) {
        return portRepository.findByIdAndStatus(id, "A")
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, NOT_FOUND));
    }

    public PortResponse create(PortRequest req) {
        Port port = Port.builder()
                .name(req.name())
                .city(req.city())
                .country(req.country())
                .portType(req.portType())
                .status("A")
                .build();
        return toResponse(portRepository.save(port));
    }

    public PortResponse update(Long id, PortRequest req) {
        Port port = portRepository.findByIdAndStatus(id, "A")
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, NOT_FOUND));

        port.setName(req.name());
        port.setCity(req.city());
        port.setCountry(req.country());
        port.setPortType(req.portType());
        portRepository.update(port);

        return toResponse(port);
    }

    public void delete(Long id) {
        portRepository.findByIdAndStatus(id, "A")
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, NOT_FOUND));
        portRepository.deleteById(id);
    }

    private PortResponse toResponse(Port p) {
        return new PortResponse(
                p.getId(),
                p.getName(),
                p.getCity(),
                p.getCountry(),
                p.getPortType(),
                p.getStatus()
        );
    }
}