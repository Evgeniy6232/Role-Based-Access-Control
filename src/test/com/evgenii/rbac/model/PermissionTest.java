package com.evgenii.rbac.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PermissionTest {

    @Test
    void createPermission_withValidData_shouldNormalize() {
        Permission perm = new Permission("read", "USERS", "Can read users");

        assertEquals("READ", perm.name());
        assertEquals("users", perm.resource());
        assertEquals("Can read users", perm.description());
    }

    @Test
    void createPermission_withSpaceInName_shouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Permission("READ ALL", "users", "Test");
        });
        assertTrue(exception.getMessage().contains("Name cannot contain space"));
    }

    @Test
    void createPermission_withEmptyDescription_shouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Permission("READ", "users", "");
        });
        assertTrue(exception.getMessage().contains("Description"));
    }

    @Test
    void format_shouldReturnCorrectString() {
        Permission perm = new Permission("read", "users", "Can read users");

        assertEquals("READ on users: Can read users", perm.format());
    }

    @Test
    void matches_withExactPatterns_shouldReturnTrue() {
        Permission perm = new Permission("READ", "users", "Test");

        assertTrue(perm.matches("READ", "users"));
    }

    @Test
    void matches_withNullPatterns_shouldReturnTrue() {
        Permission perm = new Permission("READ", "users", "Test");

        assertTrue(perm.matches(null, null));
        assertTrue(perm.matches("READ", null));
        assertTrue(perm.matches(null, "users"));
    }

    @Test
    void matches_withWrongPatterns_shouldReturnFalse() {
        Permission perm = new Permission("READ", "users", "Test");

        assertFalse(perm.matches("WRITE", "users"));
        assertFalse(perm.matches("READ", "reports"));
        assertFalse(perm.matches("WRITE", "reports"));
    }
}