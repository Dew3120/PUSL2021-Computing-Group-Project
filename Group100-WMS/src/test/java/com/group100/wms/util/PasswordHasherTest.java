package com.group100.wms.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHasherTest {

    @Test
    void hashesAndVerifiesPasswordsWithoutReturningPlainText() {
        String hash = PasswordHasher.hash("StrongPass123");

        assertNotEquals("StrongPass123", hash);
        assertTrue(PasswordHasher.verify("StrongPass123", hash));
        assertFalse(PasswordHasher.verify("WrongPass123", hash));
        assertFalse(PasswordHasher.verify(null, hash));
        assertFalse(PasswordHasher.verify("StrongPass123", null));
    }
}
