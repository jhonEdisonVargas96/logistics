package com.jhon.logistics.domain.port.out.repository.logistics;

import com.jhon.logistics.domain.model.logistics.Client;

import java.util.List;
import java.util.Optional;

public interface ClientRepository {
    List<Client> findAllActive();
    Optional<Client> findByIdAndStatus(Long id, String status);
    boolean existsByEmail(String email);
    Client save(Client client);
    void update(Client client);
    void deleteById(Long id);
}