package com.group100.wms.exception;

/**
 * Thrown when login fails — wrong username or password.
 *
 * OOP Concepts Used:
 * - Inheritance: This class extends the built-in Exception class.
 * - Encapsulation: The error message is passed and stored within the exception object.
 * - Abstraction: Provides a specific type of exception for authentication errors.
 * - No Polymorphism explicitly used in this class.
 */
public class AuthenticationException extends Exception {

    // Constructor that initializes the exception with a custom error message
    public AuthenticationException(String message) {
        super(message);
    }
}
