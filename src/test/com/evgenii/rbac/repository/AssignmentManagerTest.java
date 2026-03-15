package com.evgenii.rbac.repository;

import com.evgenii.rbac.assignment.*;
import com.evgenii.rbac.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AssignmentManagerTest {

    private UserManager userManager;
    private RoleManager roleManager;
    private AssignmentManager assignmentManager;
    private User user;
    private Role role;
    private Role userRole;
    private AssignmentMetadata metadata;

    @BeforeEach
    void setUp() {
        userManager = new UserManager();
        roleManager = new RoleManager();
        assignmentManager = new AssignmentManager(userManager, roleManager);

        user = new User("genna", "GennaQQ", "tekken1589556@gmail.com");
        role = new Role("ADMIN", "Administrator");
        userRole = new Role("USER", "Regular user");
        metadata = AssignmentMetadata.now("admin", "Test assignment");

        userManager.add(user);
        roleManager.add(role);
        roleManager.add(userRole);
    }

    @Test
    void addPermanentAssignment() {
        PermanentAssignment assignment = new PermanentAssignment(user, role, metadata);

        assignmentManager.add(assignment);

        assertEquals(1, assignmentManager.count());
        assertTrue(assignmentManager.findById(assignment.assignmentId()).isPresent());
    }

    @Test
    void addTemporaryAssignment() {
        TemporaryAssignment assignment = new TemporaryAssignment(user, role, metadata, "2026-12-31", false);

        assignmentManager.add(assignment);

        assertEquals(1, assignmentManager.count());
        assertTrue(assignmentManager.findById(assignment.assignmentId()).isPresent());
    }

    @Test
    void addDuplicateActiveAssignment_throwsException() {
        PermanentAssignment assignment1 = new PermanentAssignment(user, role, metadata);
        PermanentAssignment assignment2 = new PermanentAssignment(user, role, metadata);

        assignmentManager.add(assignment1);

        assertThrows(IllegalArgumentException.class, () ->
                assignmentManager.add(assignment2)
        );
    }

    @Test
    void addAssignment_withNonExistingUser_throwsException() {
        User nonExistingUser = new User("nonexistent", "None", "none@mail.com");
        PermanentAssignment assignment = new PermanentAssignment(nonExistingUser, role, metadata);

        assertThrows(IllegalArgumentException.class, () ->
                assignmentManager.add(assignment)
        );
    }

    @Test
    void addAssignment_withNonExistingRole_throwsException() {
        Role nonExistingRole = new Role("NONEXISTENT", "None");
        PermanentAssignment assignment = new PermanentAssignment(user, nonExistingRole, metadata);

        assertThrows(IllegalArgumentException.class, () ->
                assignmentManager.add(assignment)
        );
    }

    @Test
    void revokePermanentAssignment() {
        PermanentAssignment assignment = new PermanentAssignment(user, role, metadata);
        assignmentManager.add(assignment);

        assignmentManager.revokeAssignment(assignment.assignmentId());

        RoleAssignment found = assignmentManager.findById(assignment.assignmentId()).get();
        assertFalse(found.isActive());
    }

    @Test
    void userHasRole() {
        PermanentAssignment assignment = new PermanentAssignment(user, role, metadata);
        assignmentManager.add(assignment);

        assertTrue(assignmentManager.userHasRole(user, role));
//        assertFalse(assignmentManager.userHasRole(user, userRole));
    }

    @Test
    void getUserPermissions() {
        Permission readPerm = new Permission("READ", "users", "Read users");
        role.addPermission(readPerm);

        PermanentAssignment assignment = new PermanentAssignment(user, role, metadata);
        assignmentManager.add(assignment);

        var permissions = assignmentManager.getUserPermissions(user);

        assertEquals(1, permissions.size());
        assertTrue(permissions.contains(readPerm));
    }

    @Test
    void userHasPermission() {
        Permission readPerm = new Permission("READ", "users", "Read users");
        role.addPermission(readPerm);

        PermanentAssignment assignment = new PermanentAssignment(user, role, metadata);
        assignmentManager.add(assignment);

        assertTrue(assignmentManager.userHasPermission(user, "READ", "users"));
        assertFalse(assignmentManager.userHasPermission(user, "WRITE", "users"));
    }

    @Test
    void findByUser() {
        PermanentAssignment assignment = new PermanentAssignment(user, role, metadata);
        assignmentManager.add(assignment);

        var assignments = assignmentManager.findByUser(user);

        assertEquals(1, assignments.size());
        assertEquals(role, assignments.get(0).role());
    }

    @Test
    void getActiveAssignments() {
        PermanentAssignment active = new PermanentAssignment(user, role, metadata);
        assignmentManager.add(active);

        assignmentManager.revokeAssignment(active.assignmentId());

        var activeAssignments = assignmentManager.getActiveAssignments();

        assertEquals(0, activeAssignments.size());
    }
}