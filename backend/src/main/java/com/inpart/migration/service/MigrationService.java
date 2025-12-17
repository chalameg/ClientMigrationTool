package com.inpart.migration.service;

import com.inpart.migration.domain.Client;
import com.inpart.migration.exception.ClientAlreadyMigratedException;
import com.inpart.migration.exception.ClientNotFoundException;
import com.inpart.migration.exception.ClientNotMigratedException;
import com.inpart.migration.repository.OrganizationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer handling business logic for client migration.
 * Coordinates between repository and controller layers.
 */
@Service
public class MigrationService {
    
    private static final Logger logger = LoggerFactory.getLogger(MigrationService.class);
    
    private final OrganizationRepository repository;
    
    @Value("${migration.simulate.delay:0}")
    private long simulateDelay;

    public MigrationService(OrganizationRepository repository) {
        this.repository = repository;
    }
    
    /**
     * Simulates network latency for demo purposes.
     * Only runs if migration.simulate.delay is set in application.properties
     */
    private void simulateNetworkDelay() {
        if (simulateDelay > 0) {
            try {
                Thread.sleep(simulateDelay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("Network simulation interrupted", e);
            }
        }
    }

    /**
     * Retrieves all legacy (non-migrated) clients.
     * @return List of legacy clients
     */
    public List<Client> getLegacyClients() {
        simulateNetworkDelay();
        return repository.findByMigratedFalse();
    }

    /**
     * Retrieves all migrated clients.
     * @return List of migrated clients
     */
    public List<Client> getMigratedClients() {
        simulateNetworkDelay();
        return repository.findByMigratedTrue();
    }

    /**
     * Migrates a client by marking them as migrated.
     * @param id the client ID to migrate
     * @return the migrated client
     * @throws ClientNotFoundException if client not found
     * @throws ClientAlreadyMigratedException if client is already migrated
     */
    public Client migrateClient(Long id) {
        simulateNetworkDelay();
        
        Client client = repository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));
        
        if (client.isMigrated()) {
            throw new ClientAlreadyMigratedException(id);
        }
        
        client.setMigrated(true);
        Client migratedClient = repository.save(client);
        
        logger.info("Migrated client {} successfully", id);
        
        return migratedClient;
    }
    
    /**
     * Rolls back a client migration by marking them as not migrated.
     * @param id the client ID to rollback
     * @return the rolled back client
     * @throws ClientNotFoundException if client not found
     * @throws ClientNotMigratedException if client is not migrated
     */
    public Client rollbackMigration(Long id) {
        simulateNetworkDelay();
        
        Client client = repository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));
        
        if (!client.isMigrated()) {
            throw new ClientNotMigratedException(id);
        }
        
        client.setMigrated(false);
        Client rolledBackClient = repository.save(client);
        
        logger.info("Rolled back migration for client {} successfully", id);
        
        return rolledBackClient;
    }
}

