package com.jhon.logistics.domain.service.logistics.impl;

import com.jhon.logistics.application.dto.request.logistics.SeaShipmentRequest;
import com.jhon.logistics.application.dto.response.logistics.SeaShipmentResponse;
import com.jhon.logistics.domain.model.logistics.Client;
import com.jhon.logistics.domain.model.logistics.Port;
import com.jhon.logistics.domain.model.logistics.ProductType;
import com.jhon.logistics.domain.model.logistics.SeaShipment;
import com.jhon.logistics.domain.port.out.repository.logistics.ClientRepository;
import com.jhon.logistics.domain.port.out.repository.logistics.PortRepository;
import com.jhon.logistics.domain.port.out.repository.logistics.ProductTypeRepository;
import com.jhon.logistics.domain.port.out.repository.logistics.SeaShipmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SeaShipmentServiceImpl")
class SeaShipmentServiceImplTest {

    @Mock private SeaShipmentRepository seaShipmentRepository;
    @Mock private ClientRepository      clientRepository;
    @Mock private ProductTypeRepository productTypeRepository;
    @Mock private PortRepository        portRepository;

    @InjectMocks
    private SeaShipmentServiceImpl service;

    private SeaShipment shipment;
    private SeaShipmentRequest request;

    @BeforeEach
    void setUp() {
        shipment = SeaShipment.builder()
                .id(1L)
                .clientId(1L)
                .productTypeId(1L)
                .portId(1L)
                .quantity(5)
                .registrationDate(LocalDate.now())
                .deliveryDate(LocalDate.now().plusDays(10))
                .trackingNumber("MAR1234567")
                .fleetNumber("MAR1234A")
                .basePrice(new BigDecimal("800000.00"))
                .finalPrice(new BigDecimal("800000.00"))
                .build();

        request = new SeaShipmentRequest(
                1L, 1L, 1L, 5,
                LocalDate.now().plusDays(10),
                "MAR1234567", "MAR1234A",
                new BigDecimal("800000.00")
        );
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("returns list when shipments exist")
        void returnsListWhenShipmentsExist() {
            when(seaShipmentRepository.findAll()).thenReturn(List.of(shipment));

            List<SeaShipmentResponse> result = service.findAll();

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().trackingNumber()).isEqualTo("MAR1234567");
        }

        @Test
        @DisplayName("returns empty list when no shipments")
        void returnsEmptyList() {
            when(seaShipmentRepository.findAll()).thenReturn(List.of());

            assertThat(service.findAll()).isEmpty();
        }
    }

    @Nested
    @DisplayName("create — discount logic")
    class Create {

        private void mockValidForeignKeys() {
            when(clientRepository.findByIdAndStatus(1L, "A"))
                    .thenReturn(Optional.of(Client.builder().id(1L).build()));
            when(productTypeRepository.findByIdAndStatus(1L, "A"))
                    .thenReturn(Optional.of(ProductType.builder().id(1L).build()));
            when(portRepository.findByIdAndStatus(1L, "A"))
                    .thenReturn(Optional.of(Port.builder().id(1L).build()));
        }

        @Test
        @DisplayName("finalPrice equals basePrice when quantity <= 10")
        void noDiscountWhenQuantityTen() {
            mockValidForeignKeys();
            when(seaShipmentRepository.existsByTrackingNumber(anyString())).thenReturn(false);

            SeaShipment saved = shipment.toBuilder()
                    .quantity(10)
                    .finalPrice(new BigDecimal("800000.00"))
                    .build();
            when(seaShipmentRepository.save(any())).thenReturn(saved);

            SeaShipmentResponse result = service.create(request);

            assertThat(result.finalPrice()).isEqualByComparingTo("800000.00");
        }

        @Test
        @DisplayName("applies 3% discount when quantity > 10")
        void appliesThreePercentDiscountWhenQuantityAboveTen() {
            mockValidForeignKeys();
            when(seaShipmentRepository.existsByTrackingNumber(anyString())).thenReturn(false);

            SeaShipmentRequest req = new SeaShipmentRequest(
                    1L, 1L, 1L, 11,
                    LocalDate.now().plusDays(10),
                    "SEA9876543", "SEA5678B",
                    new BigDecimal("800000.00")
            );
            SeaShipment saved = shipment.toBuilder()
                    .quantity(11)
                    .finalPrice(new BigDecimal("776000.00"))
                    .build();
            when(seaShipmentRepository.save(any())).thenReturn(saved);

            SeaShipmentResponse result = service.create(req);

            assertThat(result.finalPrice()).isEqualByComparingTo("776000.00");
        }

        @Test
        @DisplayName("throws 409 when tracking number duplicated")
        void throws409WhenTrackingNumberDuplicated() {
            when(clientRepository.findByIdAndStatus(1L, "A"))
                    .thenReturn(Optional.of(Client.builder().id(1L).build()));
            when(productTypeRepository.findByIdAndStatus(1L, "A"))
                    .thenReturn(Optional.of(ProductType.builder().id(1L).build()));
            when(portRepository.findByIdAndStatus(1L, "A"))
                    .thenReturn(Optional.of(Port.builder().id(1L).build()));
            when(seaShipmentRepository.existsByTrackingNumber("MAR1234567")).thenReturn(true);

            assertThatThrownBy(() -> service.create(request))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("Tracking number already exists");
        }

        @Test
        @DisplayName("throws 422 when client not found")
        void throws422WhenClientNotFound() {
            when(clientRepository.findByIdAndStatus(1L, "A")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.create(request))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("Client not found or inactive");
        }

        @Test
        @DisplayName("throws 422 when product type not found")
        void throws422WhenProductTypeNotFound() {
            when(clientRepository.findByIdAndStatus(1L, "A"))
                    .thenReturn(Optional.of(Client.builder().id(1L).build()));
            when(productTypeRepository.findByIdAndStatus(1L, "A")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.create(request))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("Product type not found or inactive");
        }

        @Test
        @DisplayName("throws 422 when port not found")
        void throws422WhenPortNotFound() {
            when(clientRepository.findByIdAndStatus(1L, "A"))
                    .thenReturn(Optional.of(Client.builder().id(1L).build()));
            when(productTypeRepository.findByIdAndStatus(1L, "A"))
                    .thenReturn(Optional.of(ProductType.builder().id(1L).build()));
            when(portRepository.findByIdAndStatus(1L, "A")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.create(request))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("Port not found or inactive");
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("deletes when shipment exists")
        void deletesWhenExists() {
            when(seaShipmentRepository.findById(1L)).thenReturn(Optional.of(shipment));

            assertThatCode(() -> service.delete(1L)).doesNotThrowAnyException();
            verify(seaShipmentRepository).deleteById(1L);
        }

        @Test
        @DisplayName("throws 404 when shipment not found")
        void throws404WhenNotFound() {
            when(seaShipmentRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.delete(99L))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("Sea shipment not found");
        }
    }
}