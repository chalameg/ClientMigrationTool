package com.inpart.migration.controller;

import com.inpart.migration.domain.Client;
import com.inpart.migration.exception.ClientAlreadyMigratedException;
import com.inpart.migration.exception.ClientNotFoundException;
import com.inpart.migration.exception.ClientNotMigratedException;
import com.inpart.migration.service.MigrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for client migration operations.
 * Provides endpoints for viewing and migrating clients.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5454"})
public class MigrationController {
    
    private final MigrationService migrationService;

    public MigrationController(MigrationService migrationService) {
        this.migrationService = migrationService;
    }

    /**
     * GET /api/legacy/clients
     * Returns all legacy (non-migrated) clients.
     */
    @GetMapping("/legacy/clients")
    public ResponseEntity<List<Client>> getLegacyClients() {
        List<Client> clients = migrationService.getLegacyClients();
        return ResponseEntity.ok(clients);
    }

    /**
     * GET /api/new/clients
     * Returns all migrated clients.
     */
    @GetMapping("/new/clients")
    public ResponseEntity<List<Client>> getMigratedClients() {
        List<Client> clients = migrationService.getMigratedClients();
        return ResponseEntity.ok(clients);
    }

    /**
     * POST /api/migrate/{id}
     * Migrates a client by their ID.
     */
    @PostMapping("/migrate/{id}")
    public ResponseEntity<?> migrateClient(@PathVariable Long id) {
        try {
            Client migratedClient = migrationService.migrateClient(id);
            return ResponseEntity.ok(migratedClient);
        } catch (ClientNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (ClientAlreadyMigratedException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred"));
        }
    }
    
    /**
     * POST /api/rollback/{id}
     * Rolls back a client migration by their ID.
     */
    @PostMapping("/rollback/{id}")
    public ResponseEntity<?> rollbackMigration(@PathVariable Long id) {
        try {
            Client rolledBackClient = migrationService.rollbackMigration(id);
            return ResponseEntity.ok(rolledBackClient);
        } catch (ClientNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (ClientNotMigratedException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred"));
        }
    }
}

