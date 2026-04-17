package com.evgenii.rbac.system;

import com.evgenii.rbac.assignment.PermanentAssignment;
import com.evgenii.rbac.assignment.TemporaryAssignment;
import com.evgenii.rbac.model.AssignmentMetadata;
import com.evgenii.rbac.model.Permission;
import com.evgenii.rbac.model.Role;
import com.evgenii.rbac.model.User;
import com.evgenii.rbac.repository.*;
import com.evgenii.rbac.util.AuditLog;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RBACSystem {

    private final UserManager userManager;
    private final RoleManager roleManager;
    private final AssignmentManager assignmentManager;
    private String currentUser;
    private final AuditLog auditLog;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public void checkExpired(int periodicity) {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                List<TemporaryAssignment> expired = assignmentManager.findExpiredTemporaryAssignments();
                if (!expired.isEmpty()) {
                    for (TemporaryAssignment ta : expired) {
                        assignmentManager.deactivateAssignment(ta);
                    }
                    auditLog.log("check", "system", "expired",
                            "Deactivated " + expired.size() + " expired assignments");
                    System.out.println("Deactivated " + expired.size() + " expired assignments");
                }
            } catch (Exception e) {
                System.err.println("Error check expired: " + e.getMessage());
            }
        }, periodicity, periodicity, TimeUnit.SECONDS);
    }

    public RBACSystem() {
        this.userManager = new UserManager();
        this.roleManager = new RoleManager();
        this.assignmentManager = new AssignmentManager(userManager, roleManager);
        this.currentUser = "system";
        this.auditLog = new AuditLog();
    }

    public UserManager getUserManager() { return userManager; }
    public RoleManager getRoleManager() { return roleManager; }
    public AssignmentManager getAssignmentManager() { return assignmentManager; }
    public AuditLog getAuditLog() { return auditLog; }

    public String getCurrentUser() { return currentUser; }
    public void setCurrentUser (String username) {
        this.currentUser = username;
    }

    public void executeAsync(Runnable task) {
        executor.submit(task);
    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public void initialize() {

        Permission read = new Permission("READ", "all", "Can only read");
        Permission write = new Permission("WRITE", "all", "Can only write");
        Permission delete = new Permission("DELETE", "all", "Can only delete");
        Permission manage = new Permission("MANAGE", "all", "Management");

        Role adminRole = new Role("ADMIN", "System administrator");
        Role userRole = new Role("USER", "Base user");
        Role managerRole = new Role("MANAGER", "Management");
        Role viewerRole = new Role("Viewer", "Base viewer");

        adminRole.addPermission(read);
        adminRole.addPermission(write);
        adminRole.addPermission(delete);
        adminRole.addPermission(manage);

        userRole.addPermission(read);
        userRole.addPermission(write);

        managerRole.addPermission(read);
        managerRole.addPermission(write);
        managerRole.addPermission(manage);

        viewerRole.addPermission(read);

        roleManager.add(adminRole);
        roleManager.add(userRole);
        roleManager.add(managerRole);
        roleManager.add(viewerRole);

        User admin = new User("admin", "System administrator", "tekken1589556@gmail.com");
        userManager.add(admin);

        AssignmentMetadata meta = AssignmentMetadata.now("system", "Default admin");
        PermanentAssignment assignment = new PermanentAssignment(admin, adminRole, meta);
        assignmentManager.add(assignment);
    }

    public String generateStatistics() {

        StringBuilder stats = new StringBuilder();
        stats.append("Statistic \n");
        stats.append("Users: ").append(userManager.count()).append("\n");
        stats.append("Roles: ").append(roleManager.count()).append("\n");
        stats.append("Assignments" ).append(assignmentManager.count()).append("\n");

        long active = assignmentManager.getActiveAssignments().size();
        stats.append("Active assignment: ").append(active).append("\n");

        return stats.toString();
    }
}
