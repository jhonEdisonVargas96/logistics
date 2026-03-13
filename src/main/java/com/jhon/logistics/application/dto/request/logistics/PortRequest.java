package com.jhon.logistics.application.dto.request.logistics;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PortRequest(
        @NotBlank @Size(max = 150)                    String name,
        @NotBlank @Size(max = 100)                    String city,
        @NotBlank @Size(max = 100)                    String country,
        @NotBlank @Pattern(regexp = "^[NI]$",
                message = "portType must be N or I")      String portType
) {}
