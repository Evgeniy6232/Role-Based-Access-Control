package com.evgenii.rbac.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Set;

public class RoleTest {

    @Test
    void createRole_withValidData_shouldSucceed() {
        Role role = new Role("ADMIN", "Administrator role");

        assertNotNull(role.getId());
        assertEquals("ADMIN", role.getName());
        assertEquals("Administrator role", role.getDescription());
        assertEquals(0, role.getPermissions().size());
    }

    @Test
    void createRole_withEmptyName_shouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Role("", "Administrator role");
        });
        assertTrue(exception.getMessage().contains("Role name"));
    }

    @Test
    void addPermission_shouldIncreasePermissionCount() {
        Role role = new Role("ADMIN", "Admin");
        Permission perm = new Permission("READ", "users", "Read users");  // ← добавил description

        role.addPermission(perm);

        assertEquals(1, role.getPermissions().size());
        assertTrue(role.hasPermission(perm));
    }

    @Test
    void removePermission_shouldDecreasePermissionCount() {
        Role role = new Role("ADMIN", "Admin");
        Permission perm = new Permission("READ", "users", "Read users");  // ← добавил description

        role.addPermission(perm);
        role.removePermission(perm);

        assertEquals(0, role.getPermissions().size());
        assertFalse(role.hasPermission(perm));
    }

    @Test
    void hasPermission_byNameAndResource_shouldReturnTrueIfExists() {
        Role role = new Role("ADMIN", "Admin");
        role.addPermission(new Permission("READ", "users", "Can read users"));    // ← добавил
        role.addPermission(new Permission("WRITE", "users", "Can write users"));  // ← добавил

        assertTrue(role.hasPermission("READ", "users"));
        assertTrue(role.hasPermission("WRITE", "users"));
        assertFalse(role.hasPermission("DELETE", "users"));
        assertFalse(role.hasPermission("READ", "reports"));
    }

    @Test
    void setDescription_shouldUpdateDescription() {
        Role role = new Role("ADMIN", "Old description");

        role.setDescription("New description");

        assertEquals("New description", role.getDescription());
    }

    @Test
    void getPermissions_shouldReturnUnmodifiableSet() {
        Role role = new Role("ADMIN", "Admin");
        role.addPermission(new Permission("READ", "users", "Can read users"));  // ← добавил

        Set<Permission> perms = role.getPermissions();

        assertThrows(UnsupportedOperationException.class, () -> {
            perms.add(new Permission("WRITE", "users", "Can write users"));     // ← добавил
        });
    }
}