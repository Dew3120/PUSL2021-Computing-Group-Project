package com.group100.wms.exception;

/**
 * Wraps SQL/DB errors into a checked application exception.
 */
public class DatabaseException extends Exception {

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}