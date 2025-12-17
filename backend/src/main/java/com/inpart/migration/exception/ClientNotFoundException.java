package com.inpart.migration.exception;

/**
 * Exception thrown when a client is not found by ID.
 */
public class ClientNotFoundException extends RuntimeException {
    
    public ClientNotFoundException(Long id) {
        super("Client not found with id: " + id);
    }
    
    public ClientNotFoundException(String message) {
        super(message);
    }
}

