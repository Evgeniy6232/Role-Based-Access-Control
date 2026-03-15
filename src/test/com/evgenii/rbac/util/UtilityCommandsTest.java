package com.evgenii.rbac.command;

import com.evgenii.rbac.assignment.PermanentAssignment;
import com.evgenii.rbac.model.*;
import com.evgenii.rbac.system.RBACSystem;
import org.junit.jupiter.api.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class UtilityCommandsTest {

    private CommandParser parser;
    private RBACSystem system;
    private ByteArrayOutputStream outContent;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        parser = new CommandParser();
        system = new RBACSystem();
        CommandRegistry.registerAll(parser);

        // Создаем тестовые данные
        User genna = new User("genna", "Genna", "tekken1589556@gmail.com");
        User misha = new User("misha", "Misha", "misha@mail.ru");
        User sasha = new User("sasha", "Sasha", "sasha@gmail.com");

        system.getUserManager().add(genna);
        system.getUserManager().add(misha);
        system.getUserManager().add(sasha);

        Role admin = new Role("ADMIN", "Administrator");
        Role user = new Role("USER", "Regular user");
        Role manager = new Role("MANAGER", "Manager");

        system.getRoleManager().add(admin);
        system.getRoleManager().add(user);
        system.getRoleManager().add(manager);

        Permission read = new Permission("READ", "users", "Read users");
        Permission write = new Permission("WRITE", "users", "Write users");
        Permission delete = new Permission("DELETE", "users", "Delete users");

        admin.addPermission(read);
        admin.addPermission(write);
        admin.addPermission(delete);
        user.addPermission(read);
        manager.addPermission(read);
        manager.addPermission(write);

        AssignmentMetadata meta1 = AssignmentMetadata.now("admin", "Permanent admin");
        AssignmentMetadata meta2 = AssignmentMetadata.now("manager", "Regular user");
        AssignmentMetadata meta3 = AssignmentMetadata.now("admin", "Manager role");

        system.getAssignmentManager().add(new PermanentAssignment(genna, admin, meta1));
        system.getAssignmentManager().add(new PermanentAssignment(misha, user, meta2));
        system.getAssignmentManager().add(new PermanentAssignment(sasha, manager, meta3));

        // Логируем действия
        system.getAuditLog().log("CREATE_USER", "admin", "genna", "Created user");
        system.getAuditLog().log("CREATE_USER", "admin", "misha", "Created user");
        system.getAuditLog().log("CREATE_USER", "admin", "sasha", "Created user");
        system.getAuditLog().log("CREATE_ROLE", "admin", "ADMIN", "Created role");
        system.getAuditLog().log("CREATE_ROLE", "admin", "USER", "Created role");
        system.getAuditLog().log("CREATE_ROLE", "admin", "MANAGER", "Created role");
        system.getAuditLog().log("ASSIGN_ROLE", "admin", "genna", "Assigned ADMIN");
        system.getAuditLog().log("ASSIGN_ROLE", "manager", "misha", "Assigned USER");
        system.getAuditLog().log("ASSIGN_ROLE", "admin", "sasha", "Assigned MANAGER");

        outContent = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.out.println("\n" + "=".repeat(60));
        System.out.println(outContent.toString());
        System.out.println("=".repeat(60));
    }

    @Test
    @DisplayName("1. HELP COMMAND")
    void helpCommand_shouldCallPrintHelp() {
        parser.execute("help", new Scanner(System.in), system);
        assertTrue(outContent.toString().contains("----------Command list----------"));
    }

    @Test
    @DisplayName("2. STATS COMMAND")
    void statsCommand_shouldPrintStatistics() {
        parser.execute("stats", new Scanner(System.in), system);
        String output = outContent.toString();
        assertTrue(output.contains("<-----------Statistic system----------->"));
        assertTrue(output.contains("Users: 3 | Roles: 3"));
    }

    @Test
    @DisplayName("3. AUDIT LOG COMMAND")
    void auditLogCommand_shouldCallPrintLog() {
        parser.execute("audit-log", new Scanner(System.in), system);
        String output = outContent.toString();
        assertTrue(output.contains("<--------------- LOG --------------->"));
        assertTrue(output.contains("CREATE_USER"));
        assertTrue(output.contains("CREATE_ROLE"));
        assertTrue(output.contains("ASSIGN_ROLE"));
    }

    @Test
    @DisplayName("4. USER REPORT")
    void reportUsersCommand_shouldPrintReport() {
        parser.execute("report-users", new Scanner(System.in), system);
        String output = outContent.toString();
        assertTrue(output.contains("<----------USER REPORT---------->"));
        assertTrue(output.contains("genna"));
        assertTrue(output.contains("misha"));
        assertTrue(output.contains("sasha"));
        assertTrue(output.contains("ADMIN"));
        assertTrue(output.contains("USER"));
        assertTrue(output.contains("MANAGER"));
        assertTrue(output.contains("Total: 3"));
    }

    @Test
    @DisplayName("5. ROLE REPORT")
    void reportRolesCommand_shouldPrintReport() {
        parser.execute("report-roles", new Scanner(System.in), system);
        String output = outContent.toString();
        assertTrue(output.contains("<----------ROLE REPORT---------->"));
        assertTrue(output.contains("ADMIN"));
        assertTrue(output.contains("USER"));
        assertTrue(output.contains("MANAGER"));
        assertTrue(output.contains("Total roles: 3"));
    }

    @Test
    @DisplayName("6. PERMISSION MATRIX")
    void reportMatrixCommand_shouldPrintMatrix() {
        parser.execute("report-matrix", new Scanner(System.in), system);
        String output = outContent.toString();
        assertTrue(output.contains("<----------PERMISSION MATRIX---------->"));
        assertTrue(output.contains("genna"));
        assertTrue(output.contains("misha"));
        assertTrue(output.contains("sasha"));
        assertTrue(output.contains("users"));
    }
}