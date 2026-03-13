package com.jhon.logistics.domain.model.logistics;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class LandShipment {
    private Long          id;
    private Long          clientId;
    private Long          productTypeId;
    private Long          warehouseId;
    private Integer       quantity;
    private LocalDate registrationDate;
    private LocalDate     deliveryDate;
    private String        trackingNumber;
    private String        vehiclePlate;
    private BigDecimal    basePrice;
    private BigDecimal finalPrice;
}