package com.jhon.logistics.application.dto.response.logistics;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SeaShipmentResponse(
        Long id,
        Long clientId,
        Long productTypeId,
        Long portId,
        Integer quantity,
        LocalDate registrationDate,
        LocalDate deliveryDate,
        String trackingNumber,
        String fleetNumber,
        BigDecimal basePrice,
        BigDecimal finalPrice
) {
}
