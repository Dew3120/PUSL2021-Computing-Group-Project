package com.group100.wms.util;

import org.mindrot.jbcrypt.BCrypt;

// OOP Concepts Used:
// Encapsulation - Password hashing and verification logic is contained within this utility class
// Abstraction - Complex hashing logic is hidden behind simple methods like hash() and verify()
// Polymorphism - BCrypt methods internally use polymorphism (library-level implementation)
// Inheritance - Uses external BCrypt class which is part of a library hierarchy

public final class PasswordHasher {

    // Private constructor to prevent instantiation of this utility class
    private PasswordHasher() {}

    // Generates a secure hashed version of a plain text password using BCrypt
    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    // Verifies if a plain text password matches the previously hashed password
    public static boolean verify(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    // Main method used for testing password hashing and generating SQL update statement
    public static void main(String[] args) {
        String hash = BCrypt.hashpw("password123", BCrypt.gensalt(12));
        System.out.println("Generated hash: " + hash);
        System.out.println();
        System.out.println("UPDATE users SET password_hash = '" + hash + "';");
    }
}
