package com.jhon.logistics.application.dto.response.logistics;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LandShipmentResponse(
        Long id,
        Long clientId,
        Long productTypeId,
        Long warehouseId,
        Integer quantity,
        LocalDate registrationDate,
        LocalDate deliveryDate,
        String trackingNumber,
        String vehiclePlate,
        BigDecimal basePrice,
        BigDecimal finalPrice
) {
}
