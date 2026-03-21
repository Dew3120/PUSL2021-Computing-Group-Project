package com.group100.wms.exception;

/**
 * Thrown when payroll calculation fails —
 * e.g. missing attendance data or negative salary.
 */
public class PayrollCalculationException extends Exception {

    public PayrollCalculationException(String message) {
        super(message);
    }
}