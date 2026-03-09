package com.evgenii.rbac.assignment;

import com.evgenii.rbac.model.AssignmentMetadata;
import com.evgenii.rbac.model.Role;
import com.evgenii.rbac.model.User;

import java.time.LocalDateTime;

public class TemporaryAssignment extends AbstractRoleAssignment {

    private String expiresAt;
    private final boolean autoRenew;

    public String getExpiresAt() {  // ← ДОБАВИТЬ ЭТОТ МЕТОД
        return expiresAt;
    }

    public TemporaryAssignment (User user, Role role, AssignmentMetadata metadata,
                                String expiresAx, boolean autoRenew) {

        super(user, role, metadata);
        this.expiresAt = expiresAx;
        this.autoRenew = autoRenew;
    }

    @Override
    public boolean isActive() {
        return !isExpired();
    }

    @Override
    public String assignmentType() {
        return "TEMPORARY";
    }

    public void extend(String newExpirationDate) {
        this.expiresAt = newExpirationDate;
    }

    public boolean isExpired() {
        String compareData = LocalDateTime.now().toString();
        return compareData.compareTo(expiresAt) > 0;
    }

    @Override
    public String summary() {
        return super.summary() + "\n Expired: " + expiresAt + "\n AutoRenew: " + autoRenew;
    }

    public static void main(String[] args) {

        User user = new User("genna", "Genna", "tekken1589556@gmail.com");
        Role role = new Role("ADMIN", "Admin");
        AssignmentMetadata meta = AssignmentMetadata.now("admin", null);

        TemporaryAssignment test1 = new TemporaryAssignment(user, role, meta, "2026-12-1", false);
        System.out.println(test1.isActive());
        System.out.println(test1.summary());

    }
}