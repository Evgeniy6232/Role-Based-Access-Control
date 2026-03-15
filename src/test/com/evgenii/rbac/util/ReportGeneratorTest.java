package com.evgenii.rbac.util;

import com.evgenii.rbac.assignment.PermanentAssignment;
import com.evgenii.rbac.model.*;
import com.evgenii.rbac.repository.AssignmentManager;
import com.evgenii.rbac.repository.RoleManager;
import com.evgenii.rbac.repository.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ReportGeneratorTest {

    private ReportGenerator reportGenerator;
    private UserManager userManager;
    private RoleManager roleManager;
    private AssignmentManager assignmentManager;
    private User genna;
    private User misha;
    private Role admin;
    private Role user;
    private Permission readPerm;
    private Permission writePerm;

    @BeforeEach
    void setUp() {
        reportGenerator = new ReportGenerator();
        userManager = new UserManager();
        roleManager = new RoleManager();
        assignmentManager = new AssignmentManager(userManager, roleManager);

        genna = new User("genna", "GennaQQ", "genna@mail.com");
        misha = new User("misha", "Misha", "misha@mail.ru");
        userManager.add(genna);
        userManager.add(misha);

        admin = new Role("ADMIN", "Administrator");
        user = new Role("USER", "Regular user");
        roleManager.add(admin);
        roleManager.add(user);

        readPerm = new Permission("READ", "users", "Read users");
        writePerm = new Permission("WRITE", "users", "Write users");
        admin.addPermission(readPerm);
        admin.addPermission(writePerm);
        user.addPermission(readPerm);

        AssignmentMetadata meta = AssignmentMetadata.now("admin", "Test");
        assignmentManager.add(new PermanentAssignment(genna, admin, meta));
        assignmentManager.add(new PermanentAssignment(misha, user, meta));
    }

    @Test
    void generateUserReport_shouldContainUserInfo() {
        String report = reportGenerator.generateUserReport(userManager, assignmentManager);

        assertTrue(report.contains("genna"));
        assertTrue(report.contains("misha"));
        assertTrue(report.contains("ADMIN"));
        assertTrue(report.contains("USER"));
        assertTrue(report.contains("Total: 2"));
    }

    @Test
    void generateUserReport_withNoUsers_shouldReturnEmpty() {
        userManager.clear();
        String report = reportGenerator.generateUserReport(userManager, assignmentManager);

        assertTrue(report.contains("Total: 0"));
    }

    @Test
    void generateRoleReport_shouldContainRoleInfo() {
        String report = reportGenerator.generateRoleReport(roleManager, assignmentManager);

        assertTrue(report.contains("ADMIN"));
        assertTrue(report.contains("USER"));
        assertTrue(report.contains("2 permissions"));
        assertTrue(report.contains("1 permissions"));
        assertTrue(report.contains("Total roles: 2"));
    }

    @Test
    void generateRoleReport_withNoRoles_shouldReturnEmpty() {
        roleManager.clear();
        String report = reportGenerator.generateRoleReport(roleManager, assignmentManager);

        assertTrue(report.contains("Total roles: 0"));
    }

    @Test
    void generatePermissionMatrix_shouldContainMatrix() {
        String report = reportGenerator.generatePermissionMatrix(userManager, assignmentManager);

        assertTrue(report.contains("genna"));
        assertTrue(report.contains("misha"));
        assertTrue(report.contains("users"));
        assertTrue(report.contains("R") || report.contains("W"));
    }

    @Test
    void generatePermissionMatrix_withNoUsers_shouldReturnMessage() {
        userManager.clear();
        String report = reportGenerator.generatePermissionMatrix(userManager, assignmentManager);

        assertTrue(report.contains("No users"));
    }

    @Test
    void exportToFile_shouldNotThrow() {
        String report = reportGenerator.generateUserReport(userManager, assignmentManager);

        assertDoesNotThrow(() -> reportGenerator.exportToFile(report, "test_report.txt"));
    }
}