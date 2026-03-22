package com.group100.wms.exception;

/**
 * Thrown and audit-logged when a user attempts an action
 * outside their assigned role permissions.
 *
 * OOP Concepts Used:
 * - Inheritance: This class extends the built-in Exception class.
 * - Encapsulation: The error message is stored within the exception object.
 * - Abstraction: Represents a specific type of exception for unauthorized access scenarios.
 * - No Polymorphism explicitly used in this class.
 */
public class UnauthorizedAccessException extends Exception {

    // Constructor that initializes the exception with a custom error message
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
