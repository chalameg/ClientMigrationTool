package com.inpart.migration.exception;

/**
 * Exception thrown when attempting to migrate a client that is already migrated.
 */
public class ClientAlreadyMigratedException extends RuntimeException {
    
    public ClientAlreadyMigratedException(Long id) {
        super("Client with id " + id + " is already migrated");
    }
    
    public ClientAlreadyMigratedException(String message) {
        super(message);
    }
}

