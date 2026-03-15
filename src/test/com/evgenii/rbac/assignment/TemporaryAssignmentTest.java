package com.evgenii.rbac.assignment;

import com.evgenii.rbac.model.*;
import com.evgenii.rbac.util.DateUtils;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TemporaryAssignmentTest {

    private User createTestUser() {
        return new User("genna", "GennaQQ", "tekken1589556@gmail.com");
    }

    private Role createTestRole() {
        return new Role("ADMIN", "Administrator role");
    }

    private AssignmentMetadata createTestMetadata() {
        return AssignmentMetadata.now("admin", "Temporary assignment");
    }

    @Test
    void createTemporaryAssignment_shouldSucceed() {
        User user = createTestUser();
        Role role = createTestRole();
        AssignmentMetadata meta = createTestMetadata();
        String expiresAt = "2026-12-31";

        TemporaryAssignment assignment = new TemporaryAssignment(user, role, meta, expiresAt, false);

        assertNotNull(assignment.assignmentId());
        assertEquals(user, assignment.user());
        assertEquals(role, assignment.role());
        assertEquals(meta, assignment.metadata());
        assertEquals("TEMPORARY", assignment.assignmentType());
        assertEquals(expiresAt, assignment.getExpiresAt());
    }

    @Test
    void isActive_whenNotExpired_shouldReturnTrue() {
        String futureDate = DateUtils.addDays(DateUtils.getCurrentDate(), 30);

        TemporaryAssignment assignment = new TemporaryAssignment(
                createTestUser(), createTestRole(), createTestMetadata(), futureDate, false
        );

        assertTrue(assignment.isActive());
        assertFalse(assignment.isExpired());
    }

    @Test
    void isActive_whenExpired_shouldReturnFalse() {
        String pastDate = "2020-01-01";

        TemporaryAssignment assignment = new TemporaryAssignment(
                createTestUser(), createTestRole(), createTestMetadata(), pastDate, false
        );

        assertFalse(assignment.isActive());
        assertTrue(assignment.isExpired());
    }

    @Test
    void extend_shouldUpdateExpirationDate() {
        TemporaryAssignment assignment = new TemporaryAssignment(
                createTestUser(), createTestRole(), createTestMetadata(), "2026-12-31", false
        );

        assignment.extend("2027-12-31");

        assertEquals("2027-12-31", assignment.getExpiresAt());
    }

    @Test
    void summary_shouldIncludeExpirationInfo() {
        User user = createTestUser();
        Role role = createTestRole();
        AssignmentMetadata meta = AssignmentMetadata.now("admin", "Test reason");
        String expiresAt = "2026-12-31";

        TemporaryAssignment assignment = new TemporaryAssignment(user, role, meta, expiresAt, true);
        String summary = assignment.summary();

        assertTrue(summary.contains("[TEMPORARY]"));
        assertTrue(summary.contains("ADMIN assigned to genna"));
        assertTrue(summary.contains("by admin"));
        assertTrue(summary.contains("Reason: Test reason"));
        assertTrue(summary.contains("Expired: " + expiresAt));
        assertTrue(summary.contains("AutoRenew: true"));
    }
}