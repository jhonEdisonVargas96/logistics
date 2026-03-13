package com.jhon.logistics.domain.service.logistics.impl;

import com.jhon.logistics.application.dto.request.logistics.ClientRequest;
import com.jhon.logistics.application.dto.response.logistics.ClientResponse;
import com.jhon.logistics.domain.model.logistics.Client;
import com.jhon.logistics.domain.port.out.repository.logistics.ClientRepository;
import com.jhon.logistics.domain.service.logistics.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ClientServiceImpl implements ClientService {
    private static final String CLIENT_NOT_FOUND = "Client not found";

    private final ClientRepository clientRepository;

    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<ClientResponse> findAll() {
        return clientRepository.findAllActive().stream()
                .map(this::toResponse).toList();
    }

    public ClientResponse findById(Long id) {
        return clientRepository.findByIdAndStatus(id, "A")
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, CLIENT_NOT_FOUND));
    }

    public ClientResponse create(ClientRequest req) {
        if (clientRepository.existsByEmail(req.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }
        Client client = Client.builder()
                .name(req.name()).email(req.email())
                .phone(req.phone()).status("A")
                .build();
        return toResponse(clientRepository.save(client));
    }

    public ClientResponse update(Long id, ClientRequest req) {
        Client client = clientRepository.findByIdAndStatus(id, "A")
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, CLIENT_NOT_FOUND));
        client.setName(req.name());
        client.setEmail(req.email());
        client.setPhone(req.phone());
        clientRepository.update(client);
        return toResponse(client);
    }

    public void delete(Long id) {
        clientRepository.findByIdAndStatus(id, "A")
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, CLIENT_NOT_FOUND));
        clientRepository.deleteById(id);
    }

    private ClientResponse toResponse(Client c) {
        return new ClientResponse(c.getId(), c.getName(), c.getEmail(), c.getPhone(), c.getStatus());
    }
}
