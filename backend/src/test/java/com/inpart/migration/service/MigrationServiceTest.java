package com.inpart.migration.service;

import com.inpart.migration.domain.Client;
import com.inpart.migration.exception.ClientAlreadyMigratedException;
import com.inpart.migration.exception.ClientNotFoundException;
import com.inpart.migration.exception.ClientNotMigratedException;
import com.inpart.migration.repository.OrganizationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MigrationService.
 * Uses Mockito to mock the repository layer.
 */
@ExtendWith(MockitoExtension.class)
class MigrationServiceTest {

    @Mock
    private OrganizationRepository repository;

    @InjectMocks
    private MigrationService migrationService;

    private Client legacyClient;
    private Client migratedClient;

    @BeforeEach
    void setUp() {
        // Disable network delay for faster tests
        ReflectionTestUtils.setField(migrationService, "simulateDelay", 0L);
        
        // Setup test data
        legacyClient = new Client(1L, "Test Client", false);
        migratedClient = new Client(2L, "Migrated Client", true);
    }

    @Test
    void getLegacyClients_shouldReturnOnlyNonMigratedClients() {
        // Arrange
        List<Client> expectedClients = Arrays.asList(
            new Client(1L, "Client 1", false),
            new Client(2L, "Client 2", false)
        );
        when(repository.findByMigratedFalse()).thenReturn(expectedClients);

        // Act
        List<Client> result = migrationService.getLegacyClients();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().noneMatch(Client::isMigrated));
        verify(repository, times(1)).findByMigratedFalse();
    }

    @Test
    void getMigratedClients_shouldReturnOnlyMigratedClients() {
        // Arrange
        List<Client> expectedClients = Arrays.asList(
            new Client(1L, "Client 1", true),
            new Client(2L, "Client 2", true)
        );
        when(repository.findByMigratedTrue()).thenReturn(expectedClients);

        // Act
        List<Client> result = migrationService.getMigratedClients();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(Client::isMigrated));
        verify(repository, times(1)).findByMigratedTrue();
    }

    @Test
    void migrateClient_withValidLegacyClient_shouldSucceed() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(legacyClient));
        when(repository.save(any(Client.class))).thenReturn(legacyClient);

        // Act
        Client result = migrationService.migrateClient(1L);

        // Assert
        assertTrue(result.isMigrated());
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Client.class));
    }

    @Test
    void migrateClient_withNonExistentClient_shouldThrowClientNotFoundException() {
        // Arrange
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ClientNotFoundException.class, () -> {
            migrationService.migrateClient(999L);
        });
        verify(repository, times(1)).findById(999L);
        verify(repository, never()).save(any());
    }

    @Test
    void migrateClient_withAlreadyMigratedClient_shouldThrowClientAlreadyMigratedException() {
        // Arrange
        when(repository.findById(2L)).thenReturn(Optional.of(migratedClient));

        // Act & Assert
        assertThrows(ClientAlreadyMigratedException.class, () -> {
            migrationService.migrateClient(2L);
        });
        verify(repository, times(1)).findById(2L);
        verify(repository, never()).save(any());
    }

    @Test
    void rollbackMigration_withValidMigratedClient_shouldSucceed() {
        // Arrange
        when(repository.findById(2L)).thenReturn(Optional.of(migratedClient));
        when(repository.save(any(Client.class))).thenReturn(migratedClient);

        // Act
        Client result = migrationService.rollbackMigration(2L);

        // Assert
        assertFalse(result.isMigrated());
        verify(repository, times(1)).findById(2L);
        verify(repository, times(1)).save(any(Client.class));
    }

    @Test
    void rollbackMigration_withNonExistentClient_shouldThrowClientNotFoundException() {
        // Arrange
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ClientNotFoundException.class, () -> {
            migrationService.rollbackMigration(999L);
        });
        verify(repository, times(1)).findById(999L);
        verify(repository, never()).save(any());
    }

    @Test
    void rollbackMigration_withNonMigratedClient_shouldThrowClientNotMigratedException() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(legacyClient));

        // Act & Assert
        assertThrows(ClientNotMigratedException.class, () -> {
            migrationService.rollbackMigration(1L);
        });
        verify(repository, times(1)).findById(1L);
        verify(repository, never()).save(any());
    }
}

