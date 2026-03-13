package com.jhon.logistics.application.dto.request.logistics;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClientRequest(
        @NotBlank @Size(max = 150) String name,
        @NotBlank @Email String email,
        @Size(max = 20)            String phone
) {}
