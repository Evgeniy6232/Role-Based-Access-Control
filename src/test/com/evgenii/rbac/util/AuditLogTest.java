package com.evgenii.rbac.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class AuditLogTest {

    private AuditLog auditLog;

    @BeforeEach
    void setUp() {
        auditLog = new AuditLog();
    }

    @Test
    void log_shouldAddEntry() {
        auditLog.log("CREATE_USER", "admin", "genna", "Created user");

        assertEquals(1, auditLog.getAll().size());
    }

    @Test
    void getAll_shouldReturnAllEntries() {
        auditLog.log("CREATE_USER", "admin", "genna", "Created user");
        auditLog.log("DELETE_USER", "admin", "misha", "Deleted user");

        List<AuditEntry> entries = auditLog.getAll();

        assertEquals(2, entries.size());
    }

    @Test
    void getAll_shouldReturnCopy() {
        auditLog.log("CREATE_USER", "admin", "genna", "Created user");

        List<AuditEntry> entries = auditLog.getAll();
        entries.clear();

        assertEquals(1, auditLog.getAll().size());
    }

    @Test
    void getByPerformer_shouldReturnMatchingEntries() {
        auditLog.log("CREATE_USER", "admin", "genna", "Created user");
        auditLog.log("DELETE_USER", "admin", "misha", "Deleted user");
        auditLog.log("CREATE_USER", "manager", "sasha", "Created user");

        List<AuditEntry> adminEntries = auditLog.getByPerformer("admin");

        assertEquals(2, adminEntries.size());
        for (AuditEntry e : adminEntries) {
            assertEquals("admin", e.performer());
        }
    }

    @Test
    void getByPerformer_withNoMatches_shouldReturnEmptyList() {
        auditLog.log("CREATE_USER", "admin", "genna", "Created user");

        List<AuditEntry> result = auditLog.getByPerformer("nonexistent");

        assertTrue(result.isEmpty());
    }

    @Test
    void getByAction_shouldReturnMatchingEntries() {
        auditLog.log("CREATE_USER", "admin", "genna", "Created user");
        auditLog.log("DELETE_USER", "admin", "misha", "Deleted user");
        auditLog.log("CREATE_USER", "manager", "sasha", "Created user");

        List<AuditEntry> createEntries = auditLog.getByAction("CREATE_USER");

        assertEquals(2, createEntries.size());
        for (AuditEntry e : createEntries) {
            assertEquals("CREATE_USER", e.action());
        }
    }

    @Test
    void getByAction_withNoMatches_shouldReturnEmptyList() {
        auditLog.log("CREATE_USER", "admin", "genna", "Created user");

        List<AuditEntry> result = auditLog.getByAction("NONEXISTENT");

        assertTrue(result.isEmpty());
    }

    @Test
    void printLog_shouldNotThrow() {
        auditLog.log("CREATE_USER", "admin", "genna", "Created user");

        assertDoesNotThrow(() -> auditLog.printLog());
    }

    @Test
    void saveToFile_shouldNotThrow() {
        auditLog.log("CREATE_USER", "admin", "genna", "Created user");

        assertDoesNotThrow(() -> auditLog.saveToFile("test.log"));
    }
}