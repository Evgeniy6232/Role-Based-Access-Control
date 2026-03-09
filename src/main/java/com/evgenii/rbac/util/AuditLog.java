package com.evgenii.rbac.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuditLog {
    private final List<AuditEntry> entries = new ArrayList<>();

    public void log(String action, String performer, String target, String details) {

        String timestamp = LocalDateTime.now().toString();
        AuditEntry entry = new AuditEntry(timestamp, action, performer, target, details);
        entries.add(entry);
    }

    public List<AuditEntry> getAll() {
        return new ArrayList<>(entries);
    }

    public List<AuditEntry> getByPerformer(String performer) {
        List<AuditEntry> result = new ArrayList<>();

        for (AuditEntry entry : entries) {
            if (entry.performer().equals(performer)) {
                result.add(entry);
            }
        }

        return result;
    }

    public List<AuditEntry> getByAction(String action) {
        List<AuditEntry> result = new ArrayList<>();

        for (AuditEntry entry : entries) {
            if (entry.action().equals(action)) {
                result.add(entry);
            }
        }

        return result;
    }

    public void printLog() {
        System.out.println("<--------------- LOG --------------->");
        for (AuditEntry entry : entries) {
            System.out.println(entry.format());
        }
    }

    public void saveToFile(String filename) {

        try (PrintWriter writer = new PrintWriter(new PrintWriter(filename))) {

            for (AuditEntry entry : entries) {
                writer.println(entry.format());
            }

            System.out.println("Saved to " + filename);

        } catch (IOException e) {
            System.out.println("error saving to " + e.getMessage());
        }
    }

}
