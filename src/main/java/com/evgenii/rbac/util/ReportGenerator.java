package com.evgenii.rbac.util;

import com.evgenii.rbac.assignment.TemporaryAssignment;
import com.evgenii.rbac.model.Permission;
import com.evgenii.rbac.model.Role;
import com.evgenii.rbac.model.User;
import com.evgenii.rbac.repository.AssignmentManager;
import com.evgenii.rbac.repository.RoleManager;
import com.evgenii.rbac.repository.UserManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class ReportGenerator {

    public String generateUserReport(UserManager userManager, AssignmentManager assignmentManager) {
//        StringBuilder report = new StringBuilder("<----------USER REPORT---------->\n");
//
//        for (User user : userManager.findAll()) {
//            report.append(String.format("\n%s (%s) <%s>\n",
//                    user.username(), user.fullname(), user.email()));
//
//            var assignments = assignmentManager.findByUser(user);
//            if (assignments.isEmpty()) {
//                report.append("  No roles\n");
//                continue;
//            }
//
//            for (var a : assignments) {
//                String info = String.format("  - %s [%s] %s",
//                        a.role().getName(),
//                        a.assignmentType(),
//                        a.isActive() ? "ACTIVE" : "NOT ACTIVE");
//                report.append(info);
//
//                if (a instanceof TemporaryAssignment t) {
//                    report.append(" expires: ").append(t.getExpiresAt());
//                }
//                report.append("\n");
//            }
//        }
//
//        report.append("\nTotal: ").append(userManager.count());
//        return report.toString();

        List<User> users = userManager.findAll();

        if (users.isEmpty()) {
            return "<----------USER REPORT---------->\nTotal users: 0";
        }

        String userSection = users.parallelStream()
                .map(user -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append(String.format("\n%s (%s) <%s>\n",
                            user.username(), user.fullname(), user.email()));

                    var assignments = assignmentManager.findByUser(user);
                    if (assignments.isEmpty()) {
                        sb.append(" No roles\n");
                    } else {
                        for ( var a : assignments) {
                            String info = String.format(" - %s [%s] %s",
                                    a.role().getName(),
                                    a.assignmentType(),
                                    a.isActive() ? "ACTIVE" : "NOT ACTIVE");
                            sb.append(info);
                            if (a instanceof TemporaryAssignment t) {
                                sb.append(" expires: ").append(t.getExpiresAt());
                            }
                            sb.append("\n");
                        }
                    }
                    return sb.toString();
                }).collect(Collectors.joining());

        return "<----------USER REPORT---------->\n" + userSection + "\nTotal: " + userManager.count();
    }

    public String generateRoleReport(RoleManager roleManager, AssignmentManager assignmentManager) {
        StringBuilder report = new StringBuilder("<----------ROLE REPORT---------->\n");

        for (Role role : roleManager.findAll()) {
            long userCount = assignmentManager.findAll().stream()
                    .filter(a -> a.role().equals(role))
                    .count();

            report.append(String.format("\n%s [%d permissions] - %d users\n",
                    role.getName(),
                    role.getPermissions().size(),
                    userCount));

            if (userCount > 0) {
                report.append("  Users: ");
                assignmentManager.findAll().stream()
                        .filter(a -> a.role().equals(role))
                        .forEach(a -> report.append(a.user().username()).append(" "));
                report.append("\n");
            }
        }

        report.append("\nTotal roles: ").append(roleManager.count());
        return report.toString();
    }

    public String generatePermissionMatrix(UserManager userManager, AssignmentManager assignmentManager) {
//        StringBuilder report = new StringBuilder("<----------PERMISSION MATRIX---------->\n\n");
//
//        List<User> users = userManager.findAll();
//        if (users.isEmpty()) return report.append("No users").toString();
//
//        Set<String> resources = new HashSet<>();
//        for (User user : users) {
//            for (Permission p : assignmentManager.getUserPermissions(user)) {
//                resources.add(p.resource());
//            }
//        }
//        List<String> resourceList = new ArrayList<>(resources);
//        Collections.sort(resourceList);
//
//        report.append(String.format("%-15s", "User"));
//        for (String res : resourceList) {
//            report.append(String.format(" | %-10s", res));
//        }
//        report.append("\n" + "-".repeat(15 + resourceList.size() * 13) + "\n");
//
//        for (User user : users) {
//            report.append(String.format("%-15s", user.username()));
//
//            Map<String, String> rights = new HashMap<>();
//            for (Permission p : assignmentManager.getUserPermissions(user)) {
//                String r = rights.getOrDefault(p.resource(), "");
//                rights.put(p.resource(), r + p.name().charAt(0));
//            }
//
//            for (String res : resourceList) {
//                report.append(String.format(" | %-10s", rights.getOrDefault(res, "-")));
//            }
//            report.append("\n");
//        }
//
//        return report.toString();

        List<User> users = new ArrayList<>();
        if (users.isEmpty()) {
            return "<----------PERMISSION MATRIX---------->\nTotal users: 0";
        }

        Set<String> resources = users.parallelStream()
                .flatMap(user -> assignmentManager.getUserPermissions(user).stream())
                .map(Permission::resource)
                .collect(Collectors.toSet());

        List<String> resourceList = new ArrayList<>(resources);
        Collections.sort(resourceList);

        StringBuilder header = new StringBuilder("<----------PERMISSION MATRIX---------->\n\n");
        header.append(String.format("%-15s", "User"));
        for (String res : resourceList) {
            header.append(String.format(" | %-10s", res));
        }
        header.append("\n" + "-".repeat(15 + resourceList.size() * 13) + "\n");

        String usersSection = users.parallelStream()
                .map(user -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append(String.format("%-15s", user.username()));

                    Map<String, String> rights = new HashMap<>();
                    for (Permission p : assignmentManager.getUserPermissions(user)) {
                        String r = rights.getOrDefault(p.resource(), "");
                        rights.put(p.resource(), r + p.name().charAt(0));
                    }

                    for (String res : resourceList) {
                        sb.append(String.format(" | %-10s", rights.getOrDefault(res, "-")));
                    }
                    sb.append("\n");
                    return sb.toString();
                }).collect(Collectors.joining());

        return header.toString() + usersSection;
    }

    public void exportToFile(String report, String filename) {
        try (PrintWriter writer = new PrintWriter(filename)) {
            writer.print(report);
            System.out.println("Report saved to " + filename);
        } catch (IOException e) {
            System.out.println("Error saving report: " + e.getMessage());
        }
    }
}
