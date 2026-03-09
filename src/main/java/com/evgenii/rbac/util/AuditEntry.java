package com.evgenii.rbac.util;

public record AuditEntry(
        String timestamp, //время
        String action, //действие
        String performer, //кто
        String target, //над чем
        String details //доп.инфва
) {

    public String format() {
        return String.format("[%s] %s | %s | %s | %s",
                timestamp, performer, action, target, details);
    }

    public AuditEntry {

        if (timestamp == null || timestamp.isBlank()) {
            throw new IllegalArgumentException("Timestamp cannot be empty");
        }

        if (action == null || action.isBlank()) {
            throw new IllegalArgumentException("Action cannot be empty");
        }

        if (performer == null || performer.isBlank()) {
            throw new IllegalArgumentException("Performer cannot be empty");
        }

    }

}
