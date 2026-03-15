package com.evgenii.rbac.repository;

import com.evgenii.rbac.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class RoleManagerTest {

    private RoleManager roleManager;
    private Role adminRole;
    private Permission readPerm;

    @BeforeEach
    void setUp() {
        roleManager = new RoleManager();
        adminRole = new Role("ADMIN", "Administrator role");
        readPerm = new Permission("READ", "users", "Can read users");
    }

    @Test
    void addAndFindRole() {
        roleManager.add(adminRole);

        assertEquals(1, roleManager.count());
        assertTrue(roleManager.exists("ADMIN"));

        Optional<Role> found = roleManager.findByName("ADMIN");
        assertTrue(found.isPresent());
        assertEquals("ADMIN", found.get().getName());
    }

    @Test
    void addDuplicateRole_throwsException() {
        roleManager.add(adminRole);

        assertThrows(IllegalArgumentException.class, () ->
                roleManager.add(new Role("ADMIN", "Another admin"))
        );
    }

    @Test
    void removeRole() {
        roleManager.add(adminRole);

        boolean removed = roleManager.remove(adminRole);

        assertTrue(removed);
        assertEquals(0, roleManager.count());
        assertFalse(roleManager.findByName("ADMIN").isPresent());
    }

    @Test
    void addPermissionToRole() {
        roleManager.add(adminRole);

        roleManager.addPermissionToRole("ADMIN", readPerm);

        Role role = roleManager.findByName("ADMIN").get();
        assertTrue(role.hasPermission(readPerm));
        assertEquals(1, role.getPermissions().size());
    }

    @Test
    void removePermissionFromRole() {
        roleManager.add(adminRole);
        roleManager.addPermissionToRole("ADMIN", readPerm);

        roleManager.removePermissionFromRole("ADMIN", readPerm);

        Role role = roleManager.findByName("ADMIN").get();
        assertFalse(role.hasPermission(readPerm));
        assertEquals(0, role.getPermissions().size());
    }

    @Test
    void findRolesWithPermission() {
        roleManager.add(adminRole);
        roleManager.addPermissionToRole("ADMIN", readPerm);

        var roles = roleManager.findRolesWithPermission("READ", "users");

        assertEquals(1, roles.size());
        assertEquals("ADMIN", roles.get(0).getName());
    }
}