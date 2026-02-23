package com.uds.ged.infrastructure.exception;

/**
 * Exception thrown when attempting to register a user with a username that already exists.
 * 
 * @author GED Team
 * @version 1.0
 * @since 2026-02-22
 */
public class UsernameAlreadyExistsException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructs a new UsernameAlreadyExistsException with the specified username.
     *
     * @param username the username that already exists
     */
    public UsernameAlreadyExistsException(String username) {
        super(String.format("Username '%s' is already taken", username));
    }
    
    /**
     * Constructs a new UsernameAlreadyExistsException with a custom message.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public UsernameAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
