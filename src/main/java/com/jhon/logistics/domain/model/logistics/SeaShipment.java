package com.jhon.logistics.domain.model.logistics;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @Builder(toBuilder = true) @NoArgsConstructor @AllArgsConstructor
public class SeaShipment {
    private Long          id;
    private Long          clientId;
    private Long          productTypeId;
    private Long          portId;
    private Integer       quantity;
    private LocalDate     registrationDate;
    private LocalDate     deliveryDate;
    private String        trackingNumber;
    private String        fleetNumber;
    private BigDecimal    basePrice;
    private BigDecimal    finalPrice;
}