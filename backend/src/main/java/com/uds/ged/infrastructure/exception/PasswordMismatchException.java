package com.uds.ged.infrastructure.exception;

/**
 * Exception thrown when password and confirmation password do not match.
 * 
 * @author GED Team
 * @version 1.0
 * @since 2026-02-22
 */
public class PasswordMismatchException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructs a new PasswordMismatchException with a default message.
     */
    public PasswordMismatchException() {
        super("Password and confirmation password do not match");
    }
    
    /**
     * Constructs a new PasswordMismatchException with a custom message.
     *
     * @param message the detail message
     */
    public PasswordMismatchException(String message) {
        super(message);
    }
}
