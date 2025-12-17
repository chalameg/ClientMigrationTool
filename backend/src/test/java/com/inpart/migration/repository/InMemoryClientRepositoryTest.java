package com.inpart.migration.repository;

import com.inpart.migration.domain.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for InMemoryClientRepository.
 */
class InMemoryClientRepositoryTest {

    private InMemoryClientRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryClientRepository();
    }

    @Test
    void findAll_shouldReturnAllClients() {
        // Act
        List<Client> clients = repository.findAll();

        // Assert
        assertNotNull(clients);
        assertFalse(clients.isEmpty());
        assertEquals(6, clients.size()); // Based on sample data
    }

    @Test
    void findById_withExistingId_shouldReturnClient() {
        // Act
        Optional<Client> result = repository.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("UCB Alliance Managers", result.get().getName());
    }

    @Test
    void findById_withNonExistentId_shouldReturnEmpty() {
        // Act
        Optional<Client> result = repository.findById(999L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void findByMigratedFalse_shouldReturnOnlyLegacyClients() {
        // Act
        List<Client> legacyClients = repository.findByMigratedFalse();

        // Assert
        assertFalse(legacyClients.isEmpty());
        assertTrue(legacyClients.stream().noneMatch(Client::isMigrated));
    }

    @Test
    void findByMigratedTrue_initiallyEmpty_shouldReturnEmptyList() {
        // Act
        List<Client> migratedClients = repository.findByMigratedTrue();

        // Assert
        assertTrue(migratedClients.isEmpty());
    }

    @Test
    void save_newClient_shouldPersist() {
        // Arrange
        Client newClient = new Client(null, "New Test Client", false);

        // Act
        Client saved = repository.save(newClient);

        // Assert
        assertNotNull(saved.getId());
        assertEquals("New Test Client", saved.getName());
        
        // Verify it can be retrieved
        Optional<Client> retrieved = repository.findById(saved.getId());
        assertTrue(retrieved.isPresent());
        assertEquals(saved.getId(), retrieved.get().getId());
    }

    @Test
    void save_existingClient_shouldUpdate() {
        // Arrange
        Client client = repository.findById(1L).orElseThrow();
        client.setMigrated(true);

        // Act
        Client updated = repository.save(client);

        // Assert
        assertTrue(updated.isMigrated());
        
        // Verify persistence
        Client retrieved = repository.findById(1L).orElseThrow();
        assertTrue(retrieved.isMigrated());
    }

    @Test
    void migrationWorkflow_shouldWorkCorrectly() {
        // Initial state
        List<Client> initialLegacy = repository.findByMigratedFalse();
        List<Client> initialMigrated = repository.findByMigratedTrue();
        int initialLegacyCount = initialLegacy.size();
        int initialMigratedCount = initialMigrated.size();

        // Migrate a client
        Client client = initialLegacy.get(0);
        client.setMigrated(true);
        repository.save(client);

        // Verify counts changed
        List<Client> afterLegacy = repository.findByMigratedFalse();
        List<Client> afterMigrated = repository.findByMigratedTrue();
        
        assertEquals(initialLegacyCount - 1, afterLegacy.size());
        assertEquals(initialMigratedCount + 1, afterMigrated.size());
    }
}

