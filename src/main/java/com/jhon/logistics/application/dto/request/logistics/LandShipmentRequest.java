package com.jhon.logistics.application.dto.request.logistics;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LandShipmentRequest(
        @NotNull
        Long clientId,

        @NotNull
        Long productTypeId,

        @NotNull
        Long warehouseId,

        @NotNull @Min(1)
        Integer quantity,

        @NotNull @Future
        LocalDate deliveryDate,

        @NotBlank @Pattern(regexp = "^[A-Z0-9]{10}$",
                message = "trackingNumber must be 10 alphanumeric uppercase characters")
        String trackingNumber,

        @NotBlank @Pattern(regexp = "^[A-Z]{3}\\d{3}$",
                message = "vehiclePlate format: 3 letters + 3 digits")
        String vehiclePlate,

        @NotNull @DecimalMin("0.01")
        BigDecimal basePrice
) {}
