package com.evgenii.rbac.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ValidationUtilsTest {

    @Test
    void isValidUsername_withValidUsername_shouldReturnTrue() {
        assertTrue(ValidationUtils.isValidUsername("genna"));
        assertTrue(ValidationUtils.isValidUsername("genna_123"));
        assertTrue(ValidationUtils.isValidUsername("abc123"));
    }

    @Test
    void isValidUsername_withInvalidUsername_shouldReturnFalse() {
        assertFalse(ValidationUtils.isValidUsername("ge")); // слишком короткий
        assertFalse(ValidationUtils.isValidUsername("генна")); // русские буквы
        assertFalse(ValidationUtils.isValidUsername("genna@")); // спецсимвол
        assertFalse(ValidationUtils.isValidUsername(null));
    }

    @Test
    void isValidEmail_withValidEmail_shouldReturnTrue() {
        assertTrue(ValidationUtils.isValidEmail("tekken1589556@gmail.com"));
        assertTrue(ValidationUtils.isValidEmail("genna@mail.ru"));
        assertTrue(ValidationUtils.isValidEmail("test@test.com"));
    }

    @Test
    void isValidEmail_withInvalidEmail_shouldReturnFalse() {
        assertFalse(ValidationUtils.isValidEmail("tekken1589556gmail.com")); // без @
        assertFalse(ValidationUtils.isValidEmail("genna@mailru")); // без точки
        assertFalse(ValidationUtils.isValidEmail("@gmail.com")); // без имени
        assertFalse(ValidationUtils.isValidEmail(null));
    }

    @Test
    void isValidData_withValidDate_shouldReturnTrue() {
        assertTrue(ValidationUtils.isValidData("2026-12-31"));
        assertTrue(ValidationUtils.isValidData("2025-01-01"));
    }

    @Test
    void isValidData_withInvalidDate_shouldReturnFalse() {
        assertFalse(ValidationUtils.isValidData("31-12-2026")); // не тот формат
        assertFalse(ValidationUtils.isValidData("2026/12/31")); // слеши
        assertFalse(ValidationUtils.isValidData("abc"));
        assertFalse(ValidationUtils.isValidData(null));
    }

    @Test
    void normalizeString_shouldTrimSpaces() {
        assertEquals("genna  qq", ValidationUtils.normalizeString("  Genna  QQ  "));
    }

    @Test
    void normalizeString_shouldConvertToLowerCase() {
        assertEquals("genna qq", ValidationUtils.normalizeString("GENNA QQ"));
    }

    @Test
    void normalizeString_withNull_shouldReturnNull() {
        assertNull(ValidationUtils.normalizeString(null));
    }

    @Test
    void requireNonEmpty_withValidString_shouldNotThrow() {
        assertDoesNotThrow(() -> ValidationUtils.requireNonEmpty("genna", "Username"));
    }

    @Test
    void requireNonEmpty_withNull_shouldThrowException() {
        Exception e = assertThrows(IllegalArgumentException.class,
                () -> ValidationUtils.requireNonEmpty(null, "Username"));
        assertTrue(e.getMessage().contains("Username"));
    }

    @Test
    void requireNonEmpty_withEmptyString_shouldThrowException() {
        Exception e = assertThrows(IllegalArgumentException.class,
                () -> ValidationUtils.requireNonEmpty("", "Email"));
        assertTrue(e.getMessage().contains("Email"));
    }

    @Test
    void requireNonEmpty_withBlankString_shouldThrowException() {
        Exception e = assertThrows(IllegalArgumentException.class,
                () -> ValidationUtils.requireNonEmpty("   ", "Full name"));
        assertTrue(e.getMessage().contains("Full name"));
    }
}