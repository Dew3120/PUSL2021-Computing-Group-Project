package com.group100.wms.exception;

// OOP Concepts: Inheritance (extends Exception class), Abstraction (error details abstracted),
// Polymorphism (can be caught as Exception or specifically as PayrollCalculationException)
/**
 * Thrown when payroll calculation fails —
 * e.g. missing attendance data or negative salary.
 */
// Custom exception for payroll calculation errors with specific error messaging
public class PayrollCalculationException extends Exception {

    // Constructor accepting error message describing the calculation failure reason
    public PayrollCalculationException(String message) {
        super(message);
    }
}
