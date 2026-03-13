package com.jhon.logistics.domain.service.logistics.impl;

import com.jhon.logistics.application.dto.request.logistics.LandShipmentRequest;
import com.jhon.logistics.application.dto.response.logistics.LandShipmentResponse;
import com.jhon.logistics.domain.model.logistics.Client;
import com.jhon.logistics.domain.model.logistics.LandShipment;
import com.jhon.logistics.domain.model.logistics.ProductType;
import com.jhon.logistics.domain.model.logistics.Warehouse;
import com.jhon.logistics.domain.port.out.repository.logistics.ClientRepository;
import com.jhon.logistics.domain.port.out.repository.logistics.LandShipmentRepository;
import com.jhon.logistics.domain.port.out.repository.logistics.ProductTypeRepository;
import com.jhon.logistics.domain.port.out.repository.logistics.WarehouseRepository;
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
@DisplayName("LandShipmentServiceImpl")
class LandShipmentServiceImplTest {

    @Mock private LandShipmentRepository landShipmentRepository;
    @Mock private ClientRepository        clientRepository;
    @Mock private ProductTypeRepository   productTypeRepository;
    @Mock private WarehouseRepository     warehouseRepository;

    @InjectMocks
    private LandShipmentServiceImpl service;

    private LandShipment shipment;
    private LandShipmentRequest request;

    @BeforeEach
    void setUp() {
        shipment = LandShipment.builder()
                .id(1L)
                .clientId(1L)
                .productTypeId(1L)
                .warehouseId(1L)
                .quantity(5)
                .registrationDate(LocalDate.now())
                .deliveryDate(LocalDate.now().plusDays(3))
                .trackingNumber("ABC1234567")
                .vehiclePlate("ABC123")
                .basePrice(new BigDecimal("100000.00"))
                .finalPrice(new BigDecimal("100000.00"))
                .build();

        request = new LandShipmentRequest(
                1L, 1L, 1L, 5,
                LocalDate.now().plusDays(3),
                "ABC1234567", "ABC123",
                new BigDecimal("100000.00")
        );
    }

    // -------------------------------------------------------------------------
    // findAll
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("returns list when shipments exist")
        void returnsListWhenShipmentsExist() {
            when(landShipmentRepository.findAll()).thenReturn(List.of(shipment));

            List<LandShipmentResponse> result = service.findAll();

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().id()).isEqualTo(1L);
        }

        @Test
        @DisplayName("returns empty list when no shipments")
        void returnsEmptyListWhenNoShipments() {
            when(landShipmentRepository.findAll()).thenReturn(List.of());

            List<LandShipmentResponse> result = service.findAll();

            assertThat(result).isEmpty();
        }
    }

    // -------------------------------------------------------------------------
    // findById
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("returns shipment when found")
        void returnsShipmentWhenFound() {
            when(landShipmentRepository.findById(1L)).thenReturn(Optional.of(shipment));

            LandShipmentResponse result = service.findById(1L);

            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.trackingNumber()).isEqualTo("ABC1234567");
        }

        @Test
        @DisplayName("throws 404 when not found")
        void throws404WhenNotFound() {
            when(landShipmentRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.findById(99L))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("Land shipment not found");
        }
    }

    // -------------------------------------------------------------------------
    // create — discount logic
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("create")
    class Create {

        // Eliminamos el @BeforeEach y creamos un helper
        private void mockValidForeignKeys() {
            when(clientRepository.findByIdAndStatus(1L, "A"))
                    .thenReturn(Optional.of(Client.builder().id(1L).build()));
            when(productTypeRepository.findByIdAndStatus(1L, "A"))
                    .thenReturn(Optional.of(ProductType.builder().id(1L).build()));
            when(warehouseRepository.findByIdAndStatus(1L, "A"))
                    .thenReturn(Optional.of(Warehouse.builder().id(1L).build()));
        }

        @Test
        @DisplayName("finalPrice equals basePrice when quantity <= 10")
        void noDiscountWhenQuantityTen() {
            mockValidForeignKeys();
            when(landShipmentRepository.existsByTrackingNumber(anyString())).thenReturn(false);

            LandShipmentRequest req = new LandShipmentRequest(
                    1L, 1L, 1L, 10,
                    LocalDate.now().plusDays(3),
                    "ABC1234567", "ABC123",
                    new BigDecimal("100000.00")
            );
            LandShipment saved = LandShipment.builder()
                    .id(1L).clientId(1L).productTypeId(1L).warehouseId(1L)
                    .quantity(10).registrationDate(LocalDate.now())
                    .deliveryDate(LocalDate.now().plusDays(3))
                    .trackingNumber("ABC1234567").vehiclePlate("ABC123")
                    .basePrice(new BigDecimal("100000.00"))
                    .finalPrice(new BigDecimal("100000.00"))
                    .build();
            when(landShipmentRepository.save(any())).thenReturn(saved);

            LandShipmentResponse result = service.create(req);

            assertThat(result.finalPrice()).isEqualByComparingTo("100000.00");
        }

        @Test
        @DisplayName("applies 5% discount when quantity > 10")
        void appliesFivePercentDiscountWhenQuantityAboveTen() {
            mockValidForeignKeys();
            when(landShipmentRepository.existsByTrackingNumber(anyString())).thenReturn(false);

            LandShipmentRequest req = new LandShipmentRequest(
                    1L, 1L, 1L, 11,
                    LocalDate.now().plusDays(3),
                    "ABC1234567", "ABC123",
                    new BigDecimal("100000.00")
            );
            LandShipment saved = LandShipment.builder()
                    .id(1L).clientId(1L).productTypeId(1L).warehouseId(1L)
                    .quantity(11).registrationDate(LocalDate.now())
                    .deliveryDate(LocalDate.now().plusDays(3))
                    .trackingNumber("ABC1234567").vehiclePlate("ABC123")
                    .basePrice(new BigDecimal("100000.00"))
                    .finalPrice(new BigDecimal("95000.00"))
                    .build();
            when(landShipmentRepository.save(any())).thenReturn(saved);

            LandShipmentResponse result = service.create(req);

            assertThat(result.finalPrice()).isEqualByComparingTo("95000.00");
        }

        @Test
        @DisplayName("throws 409 when tracking number already exists")
        void throws409WhenTrackingNumberDuplicated() {
            // No necesita foreign keys — falla antes de llegar a validarlos
            when(clientRepository.findByIdAndStatus(1L, "A"))
                    .thenReturn(Optional.of(Client.builder().id(1L).build()));
            when(productTypeRepository.findByIdAndStatus(1L, "A"))
                    .thenReturn(Optional.of(ProductType.builder().id(1L).build()));
            when(warehouseRepository.findByIdAndStatus(1L, "A"))
                    .thenReturn(Optional.of(Warehouse.builder().id(1L).build()));
            when(landShipmentRepository.existsByTrackingNumber("ABC1234567")).thenReturn(true);

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
        @DisplayName("throws 422 when warehouse not found")
        void throws422WhenWarehouseNotFound() {
            when(clientRepository.findByIdAndStatus(1L, "A"))
                    .thenReturn(Optional.of(Client.builder().id(1L).build()));
            when(productTypeRepository.findByIdAndStatus(1L, "A"))
                    .thenReturn(Optional.of(ProductType.builder().id(1L).build()));
            when(warehouseRepository.findByIdAndStatus(1L, "A")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.create(request))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("Warehouse not found or inactive");
        }
    }

    // -------------------------------------------------------------------------
    // update
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("updates and returns response when shipment exists")
        void updatesWhenShipmentExists() {
            when(landShipmentRepository.findById(1L)).thenReturn(Optional.of(shipment));
            when(clientRepository.findByIdAndStatus(1L, "A"))
                    .thenReturn(Optional.of(Client.builder().id(1L).build()));
            when(productTypeRepository.findByIdAndStatus(1L, "A"))
                    .thenReturn(Optional.of(ProductType.builder().id(1L).build()));
            when(warehouseRepository.findByIdAndStatus(1L, "A"))
                    .thenReturn(Optional.of(Warehouse.builder().id(1L).build()));

            LandShipmentResponse result = service.update(1L, request);

            assertThat(result.vehiclePlate()).isEqualTo("ABC123");
            verify(landShipmentRepository).update(any(LandShipment.class));
        }

        @Test
        @DisplayName("throws 404 when shipment does not exist")
        void throws404WhenShipmentNotFound() {
            when(landShipmentRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.update(99L, request))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("Land shipment not found");
        }
    }

    // -------------------------------------------------------------------------
    // delete
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("deletes when shipment exists")
        void deletesWhenShipmentExists() {
            when(landShipmentRepository.findById(1L)).thenReturn(Optional.of(shipment));

            assertThatCode(() -> service.delete(1L)).doesNotThrowAnyException();
            verify(landShipmentRepository).deleteById(1L);
        }

        @Test
        @DisplayName("throws 404 when shipment does not exist")
        void throws404WhenNotFound() {
            when(landShipmentRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.delete(99L))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("Land shipment not found");
        }
    }
}