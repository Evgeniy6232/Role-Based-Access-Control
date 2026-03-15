package com.evgenii.rbac.sorter;

import com.evgenii.rbac.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RoleSortersTest {

    private List<Role> roles;
    private Role admin;
    private Role user;
    private Role manager;
    private Permission p1;
    private Permission p2;
    private Permission p3;

    @BeforeEach
    void setUp() {
        admin = new Role("ADMIN", "Administrator");
        user = new Role("USER", "Regular user");
        manager = new Role("MANAGER", "Manager");

        p1 = new Permission("READ", "users", "Read users");
        p2 = new Permission("WRITE", "users", "Write users");
        p3 = new Permission("DELETE", "users", "Delete users");

        admin.addPermission(p1);
        admin.addPermission(p2);
        admin.addPermission(p3);

        manager.addPermission(p1);
        manager.addPermission(p2);

        user.addPermission(p1);

        roles = new ArrayList<>();
        roles.add(manager);
        roles.add(user);
        roles.add(admin);
    }

    @Test
    void byName() {
        roles.sort(RoleSorters.byName());

        assertEquals("ADMIN", roles.get(0).getName());
        assertEquals("MANAGER", roles.get(1).getName());
        assertEquals("USER", roles.get(2).getName());
    }

    @Test
    void byPermissionCount() {
        roles.sort(RoleSorters.byPermissionCount());

        assertEquals(1, roles.get(0).getPermissions().size()); // USER
        assertEquals(2, roles.get(1).getPermissions().size()); // MANAGER
        assertEquals(3, roles.get(2).getPermissions().size()); // ADMIN
    }

    @Test
    void byName_reverseOrder() {
        roles.sort(RoleSorters.byName().reversed());

        assertEquals("USER", roles.get(0).getName());
        assertEquals("MANAGER", roles.get(1).getName());
        assertEquals("ADMIN", roles.get(2).getName());
    }

    @Test
    void byPermissionCount_reverseOrder() {
        roles.sort(RoleSorters.byPermissionCount().reversed());

        assertEquals(3, roles.get(0).getPermissions().size()); // ADMIN
        assertEquals(2, roles.get(1).getPermissions().size()); // MANAGER
        assertEquals(1, roles.get(2).getPermissions().size()); // USER
    }
}