package com.inpart.migration.repository;

import com.inpart.migration.domain.Client;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Client entities.
 * Provides abstraction for data access operations.
 */
public interface OrganizationRepository {
    
    /**
     * Find a client by their ID.
     * @param id the client ID
     * @return Optional containing the client if found
     */
    Optional<Client> findById(Long id);
    
    /**
     * Find all clients.
     * @return List of all clients
     */
    List<Client> findAll();
    
    /**
     * Find all legacy (non-migrated) clients.
     * @return List of clients where migrated = false
     */
    List<Client> findByMigratedFalse();
    
    /**
     * Find all migrated clients.
     * @return List of clients where migrated = true
     */
    List<Client> findByMigratedTrue();
    
    /**
     * Save or update a client.
     * @param client the client to save
     * @return the saved client
     */
    Client save(Client client);
}

