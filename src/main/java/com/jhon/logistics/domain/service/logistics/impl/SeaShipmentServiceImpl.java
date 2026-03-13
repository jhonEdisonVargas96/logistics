package com.jhon.logistics.domain.service.logistics.impl;

import com.jhon.logistics.application.dto.request.logistics.SeaShipmentRequest;
import com.jhon.logistics.application.dto.response.logistics.SeaShipmentResponse;
import com.jhon.logistics.domain.model.logistics.SeaShipment;
import com.jhon.logistics.domain.port.out.repository.logistics.ClientRepository;
import com.jhon.logistics.domain.port.out.repository.logistics.PortRepository;
import com.jhon.logistics.domain.port.out.repository.logistics.ProductTypeRepository;
import com.jhon.logistics.domain.port.out.repository.logistics.SeaShipmentRepository;
import com.jhon.logistics.domain.service.logistics.SeaShipmentService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class SeaShipmentServiceImpl implements SeaShipmentService {

    private static final BigDecimal SEA_DISCOUNT      = new BigDecimal("0.03");
    private static final String     SEA_NOT_FOUND     = "Sea shipment not found";
    private static final int        DISCOUNT_THRESHOLD = 10;

    private final SeaShipmentRepository seaShipmentRepository;
    private final ClientRepository clientRepository;
    private final ProductTypeRepository productTypeRepository;
    private final PortRepository portRepository;

    public SeaShipmentServiceImpl(SeaShipmentRepository seaShipmentRepository,
                                  ClientRepository clientRepository,
                                  ProductTypeRepository productTypeRepository,
                                  PortRepository portRepository) {
        this.seaShipmentRepository = seaShipmentRepository;
        this.clientRepository = clientRepository;
        this.productTypeRepository = productTypeRepository;
        this.portRepository = portRepository;
    }

    public List<SeaShipmentResponse> findAll() {
        return seaShipmentRepository.findAll().stream()
                .map(this::toResponse).toList();
    }

    public SeaShipmentResponse findById(Long id) {
        return seaShipmentRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, SEA_NOT_FOUND));
    }

    public SeaShipmentResponse create(SeaShipmentRequest req) {
        validateForeignKeys(req.clientId(), req.productTypeId(), req.portId());

        if (seaShipmentRepository.existsByTrackingNumber(req.trackingNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Tracking number already exists");
        }

        BigDecimal finalPrice = calculateFinalPrice(req.basePrice(), req.quantity());

        SeaShipment shipment = SeaShipment.builder()
                .clientId(req.clientId())
                .productTypeId(req.productTypeId())
                .portId(req.portId())
                .quantity(req.quantity())
                .registrationDate(LocalDate.now())
                .deliveryDate(req.deliveryDate())
                .trackingNumber(req.trackingNumber())
                .fleetNumber(req.fleetNumber())
                .basePrice(req.basePrice())
                .finalPrice(finalPrice)
                .build();

        return toResponse(seaShipmentRepository.save(shipment));
    }

    public SeaShipmentResponse update(Long id, SeaShipmentRequest req) {
        SeaShipment shipment = seaShipmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, SEA_NOT_FOUND));

        validateForeignKeys(req.clientId(), req.productTypeId(), req.portId());

        BigDecimal finalPrice = calculateFinalPrice(req.basePrice(), req.quantity());

        shipment.setClientId(req.clientId());
        shipment.setProductTypeId(req.productTypeId());
        shipment.setPortId(req.portId());
        shipment.setQuantity(req.quantity());
        shipment.setDeliveryDate(req.deliveryDate());
        shipment.setTrackingNumber(req.trackingNumber());
        shipment.setFleetNumber(req.fleetNumber());
        shipment.setBasePrice(req.basePrice());
        shipment.setFinalPrice(finalPrice);
        seaShipmentRepository.update(shipment);

        return toResponse(shipment);
    }

    public void delete(Long id) {
        seaShipmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, SEA_NOT_FOUND));
        seaShipmentRepository.deleteById(id);
    }

    private BigDecimal calculateFinalPrice(BigDecimal basePrice, int quantity) {
        if (quantity > DISCOUNT_THRESHOLD) {
            return basePrice.subtract(basePrice.multiply(SeaShipmentServiceImpl.SEA_DISCOUNT))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        return basePrice.setScale(2, RoundingMode.HALF_UP);
    }

    private void validateForeignKeys(Long clientId, Long productTypeId, Long portId) {
        clientRepository.findByIdAndStatus(clientId, "A")
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_CONTENT, "Client not found or inactive"));
        productTypeRepository.findByIdAndStatus(productTypeId, "A")
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_CONTENT, "Product type not found or inactive"));
        portRepository.findByIdAndStatus(portId, "A")
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_CONTENT, "Port not found or inactive"));
    }

    private SeaShipmentResponse toResponse(SeaShipment s) {
        return new SeaShipmentResponse(
                s.getId(), s.getClientId(), s.getProductTypeId(), s.getPortId(),
                s.getQuantity(), s.getRegistrationDate(), s.getDeliveryDate(),
                s.getTrackingNumber(), s.getFleetNumber(),
                s.getBasePrice(), s.getFinalPrice()
        );
    }
}
