package com.group100.wms.exception;

/**
 * Thrown and audit-logged when a user attempts an action
 * outside their assigned role permissions.
 */
public class UnauthorizedAccessException extends Exception {

    public UnauthorizedAccessException(String message) {
        super(message);
    }
}