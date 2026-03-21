package com.group100.wms.util;
import org.mindrot.jbcrypt.BCrypt;

public final class PasswordHasher {
    private PasswordHasher() {}

    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    public static boolean verify(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    public static void main(String[] args) {
        String hash = BCrypt.hashpw("password123", BCrypt.gensalt(12));
        System.out.println("Generated hash: " + hash);
        System.out.println();
        System.out.println("UPDATE users SET password_hash = '" + hash + "';");
    }
}