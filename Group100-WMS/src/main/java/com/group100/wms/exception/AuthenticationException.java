package com.group100.wms.exception;

/**
 * Thrown when login fails — wrong username or password.
 */
public class AuthenticationException extends Exception {

    public AuthenticationException(String message) {
        super(message);
    }
}