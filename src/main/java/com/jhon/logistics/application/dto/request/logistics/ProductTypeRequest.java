package com.jhon.logistics.application.dto.request.logistics;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProductTypeRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 255)           String description
) {}
