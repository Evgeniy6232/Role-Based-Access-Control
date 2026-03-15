package com.evgenii.rbac.assignment;

import com.evgenii.rbac.model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PermanentAssignmentTest {

    private User createTestUser() {
        return new User("genna", "GennaQQ", "tekken1589556@gmail.com");
    }

    private Role createTestRole() {
        return new Role("ADMIN", "Administrator role");
    }

    private AssignmentMetadata createTestMetadata() {
        return AssignmentMetadata.now("admin", "Test assignment");
    }

    @Test
    void createPermanentAssignment_shouldSucceed() {
        User user = createTestUser();
        Role role = createTestRole();
        AssignmentMetadata meta = createTestMetadata();

        PermanentAssignment assignment = new PermanentAssignment(user, role, meta);

        assertNotNull(assignment.assignmentId());
        assertEquals(user, assignment.user());
        assertEquals(role, assignment.role());
        assertEquals(meta, assignment.metadata());
        assertEquals("PERMANENT", assignment.assignmentType());
    }

    @Test
    void isActive_whenNotRevoked_shouldReturnTrue() {
        PermanentAssignment assignment = new PermanentAssignment(
                createTestUser(), createTestRole(), createTestMetadata()
        );

        assertTrue(assignment.isActive());
        assertFalse(assignment.isRevoked());
    }

    @Test
    void revoke_shouldMakeAssignmentInactive() {
        PermanentAssignment assignment = new PermanentAssignment(
                createTestUser(), createTestRole(), createTestMetadata()
        );

        assignment.revoke();

        assertFalse(assignment.isActive());
        assertTrue(assignment.isRevoked());
    }

    @Test
    void summary_shouldIncludeAllInfo() {
        User user = createTestUser();
        Role role = createTestRole();
        AssignmentMetadata meta = AssignmentMetadata.now("admin", "Test reason");

        PermanentAssignment assignment = new PermanentAssignment(user, role, meta);
        String summary = assignment.summary();

        assertTrue(summary.contains("[PERMANENT]"));
        assertTrue(summary.contains("ADMIN assigned to genna"));
        assertTrue(summary.contains("by admin"));
        assertTrue(summary.contains("Reason: Test reason"));
        assertTrue(summary.contains("Status: ACTIVE"));
    }

    @Test
    void summary_afterRevoke_shouldShowInactive() {
        PermanentAssignment assignment = new PermanentAssignment(
                createTestUser(), createTestRole(), createTestMetadata()
        );

        assignment.revoke();
        String summary = assignment.summary();

        assertTrue(summary.contains("Status: NOT ACTIVE"));
    }
}