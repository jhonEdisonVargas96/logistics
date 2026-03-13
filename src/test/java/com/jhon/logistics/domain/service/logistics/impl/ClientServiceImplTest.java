package com.jhon.logistics.domain.service.logistics.impl;

import com.jhon.logistics.application.dto.request.logistics.ClientRequest;
import com.jhon.logistics.application.dto.response.logistics.ClientResponse;
import com.jhon.logistics.domain.model.logistics.Client;
import com.jhon.logistics.domain.port.out.repository.logistics.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClientServiceImpl")
class ClientServiceImplTest {

    @Mock private ClientRepository clientRepository;

    @InjectMocks
    private ClientServiceImpl service;

    private Client client;
    private ClientRequest request;

    @BeforeEach
    void setUp() {
        client = Client.builder()
                .id(1L)
                .name("Empresa S.A.")
                .email("empresa@mail.com")
                .phone("3001234567")
                .status("A")
                .build();

        request = new ClientRequest("Empresa S.A.", "empresa@mail.com", "3001234567");
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("returns list of active clients")
        void returnsActiveClients() {
            when(clientRepository.findAllActive()).thenReturn(List.of(client));

            List<ClientResponse> result = service.findAll();

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().email()).isEqualTo("empresa@mail.com");
        }

        @Test
        @DisplayName("returns empty list when no clients")
        void returnsEmptyList() {
            when(clientRepository.findAllActive()).thenReturn(List.of());

            assertThat(service.findAll()).isEmpty();
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("returns client when found")
        void returnsClientWhenFound() {
            when(clientRepository.findByIdAndStatus(1L, "A")).thenReturn(Optional.of(client));

            ClientResponse result = service.findById(1L);

            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.name()).isEqualTo("Empresa S.A.");
        }

        @Test
        @DisplayName("throws 404 when client not found")
        void throws404WhenNotFound() {
            when(clientRepository.findByIdAndStatus(99L, "A")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.findById(99L))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("Client not found");
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("creates and returns client")
        void createsClient() {
            when(clientRepository.existsByEmail("empresa@mail.com")).thenReturn(false);
            when(clientRepository.save(any(Client.class))).thenReturn(client);

            ClientResponse result = service.create(request);

            assertThat(result.email()).isEqualTo("empresa@mail.com");
            verify(clientRepository).save(any(Client.class));
        }

        @Test
        @DisplayName("throws 409 when email already registered")
        void throws409WhenEmailDuplicated() {
            when(clientRepository.existsByEmail("empresa@mail.com")).thenReturn(true);

            assertThatThrownBy(() -> service.create(request))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("Email already registered");
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("updates and returns client")
        void updatesClient() {
            when(clientRepository.findByIdAndStatus(1L, "A")).thenReturn(Optional.of(client));

            ClientResponse result = service.update(1L, request);

            assertThat(result.name()).isEqualTo("Empresa S.A.");
            verify(clientRepository).update(any(Client.class));
        }

        @Test
        @DisplayName("throws 404 when client not found")
        void throws404WhenNotFound() {
            when(clientRepository.findByIdAndStatus(99L, "A")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.update(99L, request))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("Client not found");
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("soft deletes client when found")
        void softDeletesWhenFound() {
            when(clientRepository.findByIdAndStatus(1L, "A")).thenReturn(Optional.of(client));

            assertThatCode(() -> service.delete(1L)).doesNotThrowAnyException();
            verify(clientRepository).deleteById(1L);
        }

        @Test
        @DisplayName("throws 404 when client not found")
        void throws404WhenNotFound() {
            when(clientRepository.findByIdAndStatus(99L, "A")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.delete(99L))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("Client not found");
        }
    }
}