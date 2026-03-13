package com.jhon.logistics.application.dto.request.logistics;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WarehouseRequest(
        @NotBlank @Size(max = 150) String name,
        @NotBlank @Size(max = 255) String address,
        @NotBlank @Size(max = 100) String city
) {}
