package com.evgenii.rbac.filter;

import com.evgenii.rbac.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RoleFiltersTest {

    private Role admin;
    private Role user;
    private Role manager;
    private Permission readPerm;
    private Permission writePerm;
    private Permission deletePerm;

    @BeforeEach
    void setUp() {
        admin = new Role("ADMIN", "Administrator");
        user = new Role("USER", "Regular user");
        manager = new Role("MANAGER", "Manager");

        readPerm = new Permission("READ", "users", "Read users");
        writePerm = new Permission("WRITE", "users", "Write users");
        deletePerm = new Permission("DELETE", "users", "Delete users");

        admin.addPermission(readPerm);
        admin.addPermission(writePerm);
        admin.addPermission(deletePerm);

        user.addPermission(readPerm);

        manager.addPermission(readPerm);
        manager.addPermission(writePerm);
    }

    @Test
    void byName_exactMatch() {
        assertTrue(RoleFilters.byName("ADMIN").test(admin));
        assertTrue(RoleFilters.byName("USER").test(user));
        assertTrue(RoleFilters.byName("MANAGER").test(manager));
        assertFalse(RoleFilters.byName("ADMIN").test(user));
    }

    @Test
    void byNameContains() {
        assertTrue(RoleFilters.byNameContains("ADM").test(admin));
        assertTrue(RoleFilters.byNameContains("USE").test(user));
        assertTrue(RoleFilters.byNameContains("MAN").test(manager));
        assertTrue(RoleFilters.byNameContains("adm").test(admin));
        assertFalse(RoleFilters.byNameContains("xxx").test(admin));
    }

    @Test
    void hasPermission_byObject() {
        assertTrue(RoleFilters.hasPermission(readPerm).test(admin));
        assertTrue(RoleFilters.hasPermission(readPerm).test(user));
        assertTrue(RoleFilters.hasPermission(readPerm).test(manager));

        assertTrue(RoleFilters.hasPermission(writePerm).test(admin));
        assertFalse(RoleFilters.hasPermission(writePerm).test(user));
        assertTrue(RoleFilters.hasPermission(writePerm).test(manager));

        assertTrue(RoleFilters.hasPermission(deletePerm).test(admin));
        assertFalse(RoleFilters.hasPermission(deletePerm).test(user));
        assertFalse(RoleFilters.hasPermission(deletePerm).test(manager));
    }

    @Test
    void hasPermission_byNameAndResource() {
        assertTrue(RoleFilters.hasPermission("READ", "users").test(admin));
        assertTrue(RoleFilters.hasPermission("READ", "users").test(user));
        assertTrue(RoleFilters.hasPermission("READ", "users").test(manager));

        assertTrue(RoleFilters.hasPermission("WRITE", "users").test(admin));
        assertFalse(RoleFilters.hasPermission("WRITE", "users").test(user));
        assertTrue(RoleFilters.hasPermission("WRITE", "users").test(manager));

        assertTrue(RoleFilters.hasPermission("DELETE", "users").test(admin));
        assertFalse(RoleFilters.hasPermission("DELETE", "users").test(user));
        assertFalse(RoleFilters.hasPermission("DELETE", "users").test(manager));
    }

    @Test
    void hasAtLeastNPermissions() {
        assertTrue(RoleFilters.hasAtLeastNPermissions(3).test(admin));
        assertTrue(RoleFilters.hasAtLeastNPermissions(2).test(admin));
        assertTrue(RoleFilters.hasAtLeastNPermissions(1).test(admin));

        assertFalse(RoleFilters.hasAtLeastNPermissions(2).test(user));
        assertTrue(RoleFilters.hasAtLeastNPermissions(1).test(user));

        assertTrue(RoleFilters.hasAtLeastNPermissions(2).test(manager));
        assertFalse(RoleFilters.hasAtLeastNPermissions(3).test(manager));
    }

    @Test
    void and_combineFilters() {
        RoleFilter hasRead = RoleFilters.hasPermission("READ", "users");
        RoleFilter hasWrite = RoleFilters.hasPermission("WRITE", "users");

        assertTrue(hasRead.and(hasWrite).test(admin));
        assertTrue(hasRead.and(hasWrite).test(manager));
        assertFalse(hasRead.and(hasWrite).test(user));
    }

    @Test
    void or_combineFilters() {
        RoleFilter hasDelete = RoleFilters.hasPermission("DELETE", "users");
        RoleFilter hasWrite = RoleFilters.hasPermission("WRITE", "users");

        assertTrue(hasDelete.or(hasWrite).test(admin));
        assertTrue(hasDelete.or(hasWrite).test(manager));
        assertFalse(hasDelete.or(hasWrite).test(user));
    }

    @Test
    void complexFilter() {
        RoleFilter filter = RoleFilters.byNameContains("ADM")
                .and(RoleFilters.hasPermission("READ", "users"))
                .and(RoleFilters.hasAtLeastNPermissions(2));

        assertTrue(filter.test(admin));
        assertFalse(filter.test(user));
        assertFalse(filter.test(manager));
    }
}