package com.group100.wms.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilsTest {

    @Test
    void validatesEmailPhoneNicAndPositiveNumbers() {
        assertTrue(ValidationUtils.isValidEmail("warehouse.manager@example.com"));
        assertFalse(ValidationUtils.isValidEmail("warehouse.manager"));

        assertTrue(ValidationUtils.isValidPhone("+94 77-1234567"));
        assertFalse(ValidationUtils.isValidPhone("abc"));

        assertTrue(ValidationUtils.isValidNic("991234567V"));
        assertTrue(ValidationUtils.isValidNic("200112345678"));
        assertFalse(ValidationUtils.isValidNic("12345"));

        assertTrue(ValidationUtils.isPositiveInt("12"));
        assertFalse(ValidationUtils.isPositiveInt("0"));
        assertTrue(ValidationUtils.isPositiveDouble("12.50"));
        assertFalse(ValidationUtils.isPositiveDouble("-1"));
    }
}
