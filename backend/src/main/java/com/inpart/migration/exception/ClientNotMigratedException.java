package com.inpart.migration.exception;

/**
 * Exception thrown when attempting to rollback a client that is not migrated.
 */
public class ClientNotMigratedException extends RuntimeException {
    
    public ClientNotMigratedException(Long id) {
        super("Client with id " + id + " is not migrated, cannot rollback");
    }
    
    public ClientNotMigratedException(String message) {
        super(message);
    }
}

