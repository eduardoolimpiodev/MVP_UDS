package com.uds.ged.infrastructure.exception;

/**
 * Exception thrown when attempting to register a user with an email that already exists.
 * 
 * @author GED Team
 * @version 1.0
 * @since 2026-02-22
 */
public class EmailAlreadyExistsException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructs a new EmailAlreadyExistsException with the specified email.
     *
     * @param email the email that already exists
     */
    public EmailAlreadyExistsException(String email) {
        super(String.format("Email '%s' is already registered", email));
    }
    
    /**
     * Constructs a new EmailAlreadyExistsException with a custom message.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public EmailAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
