package com.group100.wms.exception;

/**
 * Wraps SQL/DB errors into a checked application exception.
 *
 * OOP Concepts Used:
 * - Inheritance: This class extends the built-in Exception class.
 * - Encapsulation: Error message and cause are stored within the exception object.
 * - Abstraction: Provides a custom exception type to represent database-related errors.
 * - Polymorphism: Constructor overloading is used to allow different ways of creating the exception.
 */
public class DatabaseException extends Exception {

    // Constructor that initializes the exception with a custom error message
    public DatabaseException(String message) {
        super(message);
    }

    // Constructor that initializes the exception with a message and the root cause (another throwable)
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
