package com.jhon.logistics.domain.service.logistics;

import com.jhon.logistics.application.dto.request.logistics.ClientRequest;
import com.jhon.logistics.application.dto.response.logistics.ClientResponse;

import java.util.List;

public interface ClientService {
    List<ClientResponse> findAll();
    ClientResponse findById(Long id);
    ClientResponse create(ClientRequest req);
    ClientResponse update(Long id, ClientRequest req);
    void delete(Long id);
}
