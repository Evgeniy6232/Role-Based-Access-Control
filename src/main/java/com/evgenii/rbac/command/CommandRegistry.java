package com.evgenii.rbac.command;

import com.evgenii.rbac.assignment.RoleAssignment;
import com.evgenii.rbac.assignment.TemporaryAssignment;
import com.evgenii.rbac.filter.UserFilters;
import com.evgenii.rbac.model.AssignmentMetadata;
import com.evgenii.rbac.model.Permission;
import com.evgenii.rbac.model.Role;
import com.evgenii.rbac.model.User;
import jdk.swing.interop.SwingInterOpUtils;

import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;
import java.util.*;
import java.util.function.Function;
import java.util.logging.ErrorManager;

public class CommandRegistry {

    public static void registerAll(CommandParser parser) {
        registerUserCommands(parser);
        registerRoleCommands(parser);
        registerAssignmentCommands(parser);
        registerPermissionCommands(parser);
        registerUtilityCommands(parser);
    }

    private static void registerUserCommands(CommandParser parser) {

        parser.register(new Command(
                "user-list",
                "Вывод списка пользователя\n" +
                        "--username - фильтр по имени" +
                        "--email - фильтр по email" +
                        "--domain - фильтр по domain" +
                        "--fullname - фильтр по fullaname",
                ((scanner, system, args) -> {

                    var flagSignature = Map.of(
                            "--username", 1,
                            "--email", 1,
                            "--domain", 1,
                            "--fullname", 1
                    );

                    var parsed = CommandParser.parseArgs(args, flagSignature).orElse(null);
                    if (parsed == null) {
                        ArgumentError();
                        return;
                    }

                    Function<String, Optional<String>> getFlag = key ->
                            Optional.ofNullable(parsed.flags().get(key)).map(List::getFirst);

                    var users = system.getUserManager().findAll().stream()
                            .filter(user -> getFlag.apply("--username").map(value -> UserFilters.byUsername(value).test(user)).orElse(true))
                            .filter(user -> getFlag.apply("--email").map(value -> UserFilters.byEmail(value).test(user)).orElse(true))
                            .filter(user -> getFlag.apply("--domain").map(value -> UserFilters.byEmailDomain(value).test(user)).orElse(true))
                            .filter(user -> getFlag.apply("--fullname").map(value -> UserFilters.byFullNameContains(value).test(user)).orElse(true)).toList();

                    if (users.isEmpty()) {
                        System.out.println("Пользователь не найден");
                        return;
                    }

                    System.out.println("Список пользователей: \n");
                    for (User user : users) {
                        System.out.println(user.format());
                    }
                })
        ));

        parser.register(new Command(
                "user-create",
                "Create new user",
                ((scanner, rbacSystem, args) -> {

                    if (args.size() != 3) {
                        ArgumentError();
                        return;
                    }

                    String username = args.get(0);
                    String fullname = args.get(1);
                    String email = args.get(2);

                    if (username.isEmpty() || fullname.isEmpty() || email.isEmpty()) {
                        System.out.println("Arguments should not be empty");
                        return;
                    }

                    if (!email.contains("@")) {
                        System.out.println("Email should be with @");
                        return;
                    }

                    if (rbacSystem.getUserManager().findByUsername(username).isPresent()) {
                        System.out.println("User exist");
                        return;
                    }

                    User user = new User(username, fullname, email);
                    rbacSystem.getUserManager().add(user);

                    System.out.println("User " + username + " create");
                })
        ));

        parser.register(new Command(
                "user-view",
                "Показать информацию о пользователе",
                (scanner, system, args) -> {
                    if (args.size() != 1) {
                        System.out.println("Использование: user-view <username>");
                        return;
                    }

                    String username = args.getFirst();
                    var user = system.getUserManager().findByUsername(username).orElse(null);
                    if (user != null) {
                        System.out.println(user);
                    } else {
                        System.out.println("Пользователь не найден");
                    }
                }
        ));

        parser.register(new Command(
                "user-update",
                "Обновлние пользователя",
                (scanner, rbacSystem, args) -> {

                    if (args.isEmpty()) {
                        ArgumentError();
                        return;
                    }

                    var flagSignature = Map.of("--fullname", 1, "--email", 1);
                    var parsed = CommandParser.parseArgs(args, flagSignature).orElse(null);

                    if (parsed == null || parsed.baseArgs().isEmpty()) {
                        ArgumentError();
                        return;
                    }

                    String username = parsed.baseArgs().getFirst();

                    var userOpt = rbacSystem.getUserManager().findByUsername(username);
                    if (userOpt.isEmpty()) {
                        System.out.println("User should exist");
                        return;
                    }

                    User user = userOpt.get();

                    Function<String, Optional<String>> getFlag = key ->
                            Optional.ofNullable(parsed.flags().get(key))
                                    .map(List::getFirst);

                    String newFullName = getFlag.apply("--fullname").orElse(user.fullname());
                    String newEmail = getFlag.apply("--email").orElse(user.email());

                    try {
                        rbacSystem.getUserManager().update(username, newFullName, newEmail);
                        System.out.println("User " + username + "update");
                    } catch (IllegalArgumentException e) {
                        System.out.println("error " + e.getMessage());
                    }
                }
        ));

        parser.register(new Command(
                "user-delete",
                "Удаление пользователя",
                (scanner, rbacSystem, args) -> {

                    if (args.size() != 1) {
                        ArgumentError();
                        return;
                    }

                    String username = args.getFirst();

                    var userOpt = rbacSystem.getUserManager().findByUsername(username);
                    if (userOpt.isEmpty()) {
                        System.out.println("User not found");
                        return;
                    }

                    User user = userOpt.get();

                    System.out.println("User " + user.username());
                    System.out.println("fullname " + user.fullname());
                    System.out.println("Email " + user.email());

                    var assignments = rbacSystem.getAssignmentManager().findByUser(user);
                    if (!assignments.isEmpty()) {
                        System.out.println("Current assignments " + assignments.size());
                        for (var a : assignments) {
                            System.out.println(" -" + a.role().getName());
                        }
                    }

                    System.out.print("Confirm deletion 'Yes' ");
                    String confirm = scanner.nextLine().trim();
                    if (!confirm.equals("Yes")) {
                        System.out.println("deleting canceled");
                        return;
                    }

                    int countRemove = 0;
                    for (var assignment : assignments) {
                        if (rbacSystem.getAssignmentManager().remove(assignment)) {
                            countRemove++;
                        }
                    }

                    rbacSystem.getUserManager().remove(user);
                    System.out.println("User '" + username + "' remove\n");
                    System.out.println("Assignment delete: " + countRemove);
                }
        ));


    }

    private static void registerRoleCommands(CommandParser parser) {

        parser.register(new Command(
                "role-list",
                "Вывести список всех ролей",
                (scanner, rbacSystem, args) -> {

                    if (args.isEmpty()) {
                        ArgumentError();
                        return;
                    }

                    var roles = rbacSystem.getRoleManager().findAll();
                    if (roles.isEmpty()) {
                        System.out.println("Roles don't exist");
                        return;
                    }

                    System.out.println("----------Role list----------");
                    for (Role role : roles) {
                        System.out.printf(" - %s | Assignments: &d | ID: %s%m",
                                role.getName(),
                                role.getPermissions().size(),
                                role.getId()
                        );
                    }
                    System.out.println("All roles: " + roles.size());


                }
        ));

        parser.register(new Command(
                "role-create",
                "Create new role",
                ((scanner, rbacSystem, args) -> {

                    var parsed = CommandParser.parseArgs(args, Collections.emptyMap()).orElse(null);

                    if (parsed == null || parsed.baseArgs().size() != 1) {
                        ArgumentError();
                        return;
                    }

                    String roleName = parsed.baseArgs().getFirst();

                    if (rbacSystem.getRoleManager().exists(roleName)) {
                        System.out.println("error: role " + roleName + "exist");
                        return;
                    }

                    System.out.println("Input description: ");
                    String description = scanner.nextLine().trim();

                    Role role = new Role(roleName, description);
                    rbacSystem.getRoleManager().add(role);
                    System.out.println("Role " + roleName + "create");

                    System.out.println("Add permission? (y/n)");
                    if (scanner.nextLine().trim().equalsIgnoreCase("y")) {

                        while (true) {
                            System.out.println("Input name permission or 'stop' :");
                            String permName = scanner.nextLine().trim();
                            if (permName.equalsIgnoreCase("stop")) break;

                            System.out.println("resource: ");
                            String resource = scanner.nextLine().trim();
                            System.out.println("description: ");
                            String permDesc = scanner.nextLine().trim();

                            try {
                                Permission perm = new Permission(permName, resource, permDesc);
                                rbacSystem.getRoleManager().addPermissionToRole(roleName, perm);
                                System.out.println("Permission added");
                            } catch (IllegalArgumentException e) {
                                System.out.println("Error: " + e.getMessage());
                            }
                        }
                    }

                    System.out.println(role.format());
                })
        ));

        parser.register(new Command(
                "role-view",
                "Show role",
                ((scanner, rbacSystem, args) -> {

                    var parsed = CommandParser.parseArgs(args, Collections.emptyMap()).orElse(null);

                    if (parsed == null || parsed.baseArgs().size() != 1) {
                        ArgumentError();
                        return;
                    }

                    String roleName = parsed.baseArgs().getFirst();

                    var roleOpt = rbacSystem.getRoleManager().findByName(roleName);
                    if (roleOpt.isEmpty()) {
                        System.out.println("Role dont exist");
                        return;
                    }

                    System.out.println(roleOpt.get().format());
                })
        ));

        parser.register(new Command(
                "role-update",
                "Update description role",
                ((scanner, rbacSystem, args) -> {

                    var parsed = CommandParser.parseArgs(args, Map.of()).orElse(null);
                    if (parsed == null || parsed.baseArgs().size() != 2) {
                        ArgumentError();
                        return;
                    }

                    String roleName = parsed.baseArgs().get(0);
                    String newDescription = parsed.baseArgs().get(1);

                    var roleOpt = rbacSystem.getRoleManager().findByName(roleName);
                    if (roleOpt.isEmpty()) {
                        System.out.println("role dont exist");
                        return;
                    }

                    roleOpt.get().setDescription(newDescription);
                    System.out.println("Description role '" + roleName + "' update");
                })
        ));

        parser.register(new Command(
                "role-delete",
                "Delete role",
                ((scanner, rbacSystem, args) -> {

                    var parsed = CommandParser.parseArgs(args, Map.of()).orElse(null);
                    if (parsed == null || parsed.baseArgs().size() != 2) {
                        ArgumentError();
                        return;
                    }

                    String roleName = parsed.baseArgs().get(0);
                    String newDescription = parsed.baseArgs().get(1);

                    var roleOpt = rbacSystem.getRoleManager().findByName(roleName);
                    if (roleOpt.isEmpty()) {
                        System.out.println("role dont exist");
                        return;
                    }

                    Role role = roleOpt.get();
                    var assignment = rbacSystem.getAssignmentManager().findByRole(role);
                    if (!assignment.isEmpty()) {
                        System.out.println("Role exist");

                        System.out.println("Delete role? (y/n)");
                        String confirm = scanner.nextLine().trim();
                        if (!confirm.equalsIgnoreCase("y")) {
                            System.out.println("Delete canceled");
                            return;
                        }
                    }
                    rbacSystem.getRoleManager().remove(role);
                    System.out.println("Role '" + roleName + "' deleted");
                })
        ));

        parser.register(new Command(
                "role-add-permission",
                "Add permission for role",
                ((scanner, rbacSystem, args) -> {

                    var signFlag = Map.of(
                            "--name", 1,
                            "--resource", 1,
                            "--description", 1
                    );

                    var parsed = CommandParser.parseArgs(args, signFlag).orElse(null);

                    if (parsed == null || parsed.baseArgs().isEmpty()) {
                        ArgumentError();
                        return;
                    }

                    String roleName = parsed.baseArgs().getFirst();

                    var flags = parsed.flags();
                    if (!flags.containsKey("--name") || !flags.containsKey("--resource") || !flags.containsKey("--description")) {
                        System.out.println("error: incorrect input flags");
                        return;
                    }

                    String permName = flags.get("--name").getFirst();
                    String resource = flags.get("--resource").getFirst();
                    String description = flags.get("--description").getFirst();

                    if (!rbacSystem.getRoleManager().exists(roleName)) {
                        System.out.println("Role not found");
                        return;
                    }

                    try {
                        Permission permission = new Permission(permName, resource, description);
                        rbacSystem.getRoleManager().addPermissionToRole(roleName, permission);
                        System.out.println("Permisiion '" + permName + "'  added for role");
                    } catch (IllegalArgumentException e) {
                        System.out.println("error " + e.getMessage());
                    }
                })
        ));

        parser.register(new Command(
                "role-remove-description",
                "Remove description for role",
                ((scanner, rbacSystem, args) -> {

                    var parsed = CommandParser.parseArgs(args, Map.of()).orElse(null);
                    if (parsed == null || parsed.baseArgs().size() != 2) {
                        ArgumentError();
                        return;
                    }

                    String roleName = parsed.baseArgs().get(0);
                    String newDescription = parsed.baseArgs().get(1);

                    var roleOpt = rbacSystem.getRoleManager().findByName(roleName);
                    if (roleOpt.isEmpty()) {
                        System.out.println("role don't exist");
                        return;
                    }

                    Role role = roleOpt.get();


                    var permissions = role.getPermissions();
                    if (permissions.isEmpty()) {
                        System.out.println("role don't have permission");
                        return;
                    }

                    System.out.println("Permission for role '" + roleName + "' :");
                    List<Permission> permList = new ArrayList<>(permissions);

                    for (int i = 0; i < permList.size(); i++) {
                        Permission p = permList.get(i);
                        System.out.printf("  %d. %s on %s: %s%n",
                                i + 1, p.name(), p.resource(), p.description());
                    }

                    System.out.println("Enter the number permission to delete");
                    String input = scanner.nextLine().trim();

                    try {
                        int index = Integer.parseInt(input) - 1;

                        if (index < 0 || index >= permList.size()) {
                            System.out.println("Error: incorrect number");
                            return;
                        }

                        Permission toRemove = permList.get(index);
                        rbacSystem.getRoleManager().removePermissionFromRole(roleName, toRemove);
                        System.out.println("Permission '" + toRemove.name() + "' deleted");

                    } catch (IllegalArgumentException e) {
                        System.out.println("ERROR: " + e.getMessage());
                    }
                })
        ));

        parser.register(new Command(
                "role-search",
                "Search by filters",
                (scanner, rbacSystem, args) -> {

                    var signatureFlags = Map.of(
                            "--name", 1,
                            "--permission", 1,
                            "--minPermission", 1
                    );

                    var parsed = CommandParser.parseArgs(args, signatureFlags).orElse(null);
                    if (parsed == null) {
                        System.out.println("error incorrect flags");
                        return;
                    }

                    Function<String, Optional<String>> getFlag = key ->
                            Optional.ofNullable(parsed.flags().get(key)).map(List::getFirst);

                    var roles = rbacSystem.getRoleManager().findAll().stream()
                            .filter(r -> getFlag.apply("--name").map(v -> r.getName().contains(v)).orElse(true))
                            .filter(r -> getFlag.apply("--permission").map(v -> r.hasPermission(v, "")).orElse(true))
                            .filter(r -> {
                                var min = getFlag.apply("--minPermission");
                                if (min.isPresent()) {
                                    try {
                                        return r.getPermissions().size() >= Integer.parseInt(min.get());
                                    } catch (NumberFormatException e) {
                                        return true;
                                    }
                                }

                                return true;
                            }).toList();

                    if (roles.isEmpty()) {
                        System.out.println("Roles don't exists");
                        return;
                    }

                    System.out.println("Found " + roles.size());
                    roles.forEach(r -> System.out.println(" -" + r.getName() + " (" + r.getPermissions().size() + " permissions"));
                }
        ));
    }

    private static void registerAssignmentCommands(CommandParser parser) {

        parser.register(new Command(
                "assign-role",
                "Назначить роль пользователю\n" +
                        "--user - имя пользователя\n" +
                        "--role - название роли\n" +
                        "--temp - дата истечения YYYY-MM-DD\n" +
                        "--reason - причина назначения",
                (scanner, rbacSystem, args) -> {

                    var flagSignature = Map.of(
                            "--user", 1,
                            "--role", 1,
                            "--temp", 1,
                            "--reason", 1
                    );

                    var parsed = CommandParser.parseArgs(args, flagSignature).orElse(null);
                    if (parsed == null) {
                        System.out.println("error incorrect flags");
                        return;
                    }

                    Function<String, Optional<String>> getFlag = key ->
                            Optional.ofNullable(parsed.flags().get(key)).map(List::getFirst);

                    String username = getFlag.apply("--user").orElse(null);
                    String roleName = getFlag.apply("--role").orElse(null);

                    if (username == null || roleName == null) {
                        System.out.println("Error: Username and Role must exist");
                        return;
                    }

                    var userOpt = rbacSystem.getUserManager().findByUsername(username);
                    if (userOpt.isEmpty()) {
                        System.out.println("User don't found");
                        return;
                    }

                    var roleOpt = rbacSystem.getRoleManager().findByName(roleName);
                    if (roleOpt.isEmpty()) {
                        System.out.println("Role don't found");
                        return;
                    }

                    var user = userOpt.get();
                    var role = roleOpt.get();

                    if (rbacSystem.getAssignmentManager().userHasRole(user, role)) {
                        System.out.println("User already has a role");
                        return;
                    }

                    String reason = getFlag.apply("--reason").orElse("For no reason");
                    var meta = AssignmentMetadata.now(rbacSystem.getCurrentUser(), reason);

                    RoleAssignment assignment;

                    var tempData = getFlag.apply("--temp");
                    if (tempData.isPresent()) {
                        assignment = new TemporaryAssignment(user, role, meta, tempData.get(), false);
                        System.out.println("Added temporary assignment");
                    }
                }
                ));

        parser.register(new Command(
                "revoke-role",
                "Отозвать роль у пользователя\n" +
                        "--user - имя пользователя\n" +
                        "--role - название роли",
                (scanner, system, args) -> {

                    var flagSignature = Map.of(
                            "--user", 1,
                            "--role", 1
                    );

                    var parsed = CommandParser.parseArgs(args, flagSignature).orElse(null);
                    if (parsed == null) {
                        ArgumentError();
                        return;
                    }

                    java.util.function.Function<String, java.util.Optional<String>> getFlag = key ->
                            java.util.Optional.ofNullable(parsed.flags().get(key)).map(List::getFirst);

                    String username = getFlag.apply("--user").orElse(null);
                    String roleName = getFlag.apply("--role").orElse(null);

                    if (username == null || roleName == null) {
                        System.out.println("Ошибка: --user и --role обязательны");
                        return;
                    }

                    var userOpt = system.getUserManager().findByUsername(username);
                    if (userOpt.isEmpty()) {
                        System.out.println("Пользователь не найден: " + username);
                        return;
                    }

                    var user = userOpt.get();

                    var assignments = system.getAssignmentManager().findByUser(user);
                    var assignmentOpt = assignments.stream()
                            .filter(a -> a.role().getName().equalsIgnoreCase(roleName) && a.isActive())
                            .findFirst();

                    if (assignmentOpt.isEmpty()) {
                        System.out.println("У пользователя нет активной роли: " + roleName);
                        return;
                    }

                    var assignment = assignmentOpt.get();
                    system.getAssignmentManager().revokeAssignment(assignment.assignmentId());
                    System.out.println("Роль " + roleName + " отозвана у пользователя " + username);
                }
        ));

        parser.register(new Command(
                "assignment-list-user",
                "Assignment list user",
                (scanner, rbacSystem, args) -> {

                    var parsed = CommandParser.parseArgs(args, Map.of("--user", 1)).orElse(null);
                    if (parsed == null) {
                        ArgumentError();
                        return;
                    }

                    String username = parsed.flags().get("--user").getFirst();
                    var userOpt = rbacSystem.getUserManager().findByUsername(username);
                    if (userOpt.isEmpty()) {
                        System.out.println("USer don't exist");
                        return;
                    }

                    var assignments = rbacSystem.getAssignmentManager().findByUser(userOpt.get());
                    if (assignments.isEmpty()) {
                        System.out.println("User don't have assignments");
                        return;
                    }

                    System.out.println("Assignments " + username + " :");
                    for (var a : assignments) {
                        String type = a instanceof TemporaryAssignment ? "TEMPORARY" : "PERMANENT";

                        String status = a.isActive() ? "ACTIVE" : "NOT ACTIVE";
                        System.out.printf("[%s] %s | %s | %s%n  %s%n",
                                a.assignmentId(), a.role().getName(), type, status, a.metadata().format());
                    }

                }
        ));

        parser.register(new Command("assignment-list", "Список всех назначений", (scanner, system, args) -> {

            var assignments = system.getAssignmentManager().findAll();
            if (assignments.isEmpty()) {
                System.out.println("Нет назначений");
                return;
            }

            System.out.println("\nВсе назначения:");
            for (var a : assignments) {
                String type = a instanceof com.evgenii.rbac.assignment.TemporaryAssignment ? "TEMPORARY" : "PERMANENT";
                String status = a.isActive() ? "ACTIVE" : "INACTIVE";
                System.out.printf("[%s] %s -> %s | %s | %s%n  %s%n", a.assignmentId(), a.user().username(), a.role().getName(), type, status, a.metadata().format());
            }
        }
        ));



    }

    private static void registerPermissionCommands(CommandParser parser) {

    }

    private static void registerUtilityCommands(CommandParser parser) {

    }

    private static void ArgumentError() {
        System.out.println("Error: incorrect number of arguments");
    }
}
