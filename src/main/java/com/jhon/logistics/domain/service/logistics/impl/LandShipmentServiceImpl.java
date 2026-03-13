package com.jhon.logistics.domain.service.logistics.impl;

import com.jhon.logistics.application.dto.request.logistics.LandShipmentRequest;
import com.jhon.logistics.application.dto.response.logistics.LandShipmentResponse;
import com.jhon.logistics.domain.model.logistics.LandShipment;
import com.jhon.logistics.domain.port.out.repository.logistics.ClientRepository;
import com.jhon.logistics.domain.port.out.repository.logistics.LandShipmentRepository;
import com.jhon.logistics.domain.port.out.repository.logistics.ProductTypeRepository;
import com.jhon.logistics.domain.port.out.repository.logistics.WarehouseRepository;
import com.jhon.logistics.domain.service.logistics.LandShipmentService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class LandShipmentServiceImpl implements LandShipmentService {

    private static final BigDecimal LAND_DISCOUNT    = new BigDecimal("0.05");
    private static final String LAND_NOT_FOUND   = "Land shipment not found";
    private static final int        DISCOUNT_THRESHOLD = 10;

    private final LandShipmentRepository landShipmentRepository;
    private final ClientRepository clientRepository;
    private final ProductTypeRepository productTypeRepository;
    private final WarehouseRepository warehouseRepository;

    public LandShipmentServiceImpl(LandShipmentRepository landShipmentRepository, 
                                   ClientRepository clientRepository, 
                                   ProductTypeRepository productTypeRepository, 
                                   WarehouseRepository warehouseRepository) {
        this.landShipmentRepository = landShipmentRepository;
        this.clientRepository = clientRepository;
        this.productTypeRepository = productTypeRepository;
        this.warehouseRepository = warehouseRepository;
    }

    public List<LandShipmentResponse> findAll() {
        return landShipmentRepository.findAll().stream()
                .map(this::toResponse).toList();
    }

    public LandShipmentResponse findById(Long id) {
        return landShipmentRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, LAND_NOT_FOUND));
    }

    public LandShipmentResponse create(LandShipmentRequest req) {
        validateForeignKeys(req.clientId(), req.productTypeId(), req.warehouseId());

        if (landShipmentRepository.existsByTrackingNumber(req.trackingNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Tracking number already exists");
        }

        BigDecimal finalPrice = calculateFinalPrice(req.basePrice(), req.quantity());

        LandShipment shipment = LandShipment.builder()
                .clientId(req.clientId())
                .productTypeId(req.productTypeId())
                .warehouseId(req.warehouseId())
                .quantity(req.quantity())
                .registrationDate(LocalDate.now())
                .deliveryDate(req.deliveryDate())
                .trackingNumber(req.trackingNumber())
                .vehiclePlate(req.vehiclePlate())
                .basePrice(req.basePrice())
                .finalPrice(finalPrice)
                .build();

        return toResponse(landShipmentRepository.save(shipment));
    }

    public LandShipmentResponse update(Long id, LandShipmentRequest req) {
        LandShipment shipment = landShipmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, LAND_NOT_FOUND));

        validateForeignKeys(req.clientId(), req.productTypeId(), req.warehouseId());

        BigDecimal finalPrice = calculateFinalPrice(req.basePrice(), req.quantity());

        shipment.setClientId(req.clientId());
        shipment.setProductTypeId(req.productTypeId());
        shipment.setWarehouseId(req.warehouseId());
        shipment.setQuantity(req.quantity());
        shipment.setDeliveryDate(req.deliveryDate());
        shipment.setTrackingNumber(req.trackingNumber());
        shipment.setVehiclePlate(req.vehiclePlate());
        shipment.setBasePrice(req.basePrice());
        shipment.setFinalPrice(finalPrice);
        landShipmentRepository.update(shipment);

        return toResponse(shipment);
    }

    public void delete(Long id) {
        landShipmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, LAND_NOT_FOUND));
        landShipmentRepository.deleteById(id);
    }

    /**
     * Applies discount if quantity exceeds a threshold, otherwise returns base price.
     */
    private BigDecimal calculateFinalPrice(BigDecimal basePrice, int quantity) {
        if (quantity > DISCOUNT_THRESHOLD) {
            return basePrice.subtract(basePrice.multiply(LandShipmentServiceImpl.LAND_DISCOUNT))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        return basePrice.setScale(2, RoundingMode.HALF_UP);
    }

    private void validateForeignKeys(Long clientId, Long productTypeId, Long warehouseId) {
        clientRepository.findByIdAndStatus(clientId, "A")
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_CONTENT, "Client not found or inactive"));
        productTypeRepository.findByIdAndStatus(productTypeId, "A")
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_CONTENT, "Product type not found or inactive"));
        warehouseRepository.findByIdAndStatus(warehouseId, "A")
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_CONTENT, "Warehouse not found or inactive"));
    }

    private LandShipmentResponse toResponse(LandShipment s) {
        return new LandShipmentResponse(
                s.getId(), s.getClientId(), s.getProductTypeId(), s.getWarehouseId(),
                s.getQuantity(), s.getRegistrationDate(), s.getDeliveryDate(),
                s.getTrackingNumber(), s.getVehiclePlate(),
                s.getBasePrice(), s.getFinalPrice()
        );
    }
}
