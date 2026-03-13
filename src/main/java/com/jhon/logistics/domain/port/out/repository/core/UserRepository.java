package com.jhon.logistics.domain.port.out.repository.core;

import com.jhon.logistics.domain.model.core.User;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmailAndStatus(String email, String status);
    boolean existsByEmail(String email);
    void save(User user);
}