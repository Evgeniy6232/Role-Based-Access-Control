package com.evgenii.rbac.command;

import com.evgenii.rbac.filter.UserFilters;
import com.evgenii.rbac.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class CommandRegistry {

    public static void registerAll (CommandParser parser) {
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

                    if (username.isEmpty() || fullname.isEmpty() || email.isEmpty() ){
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
                ((scanner, rbacSystem, args) -> {

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

                    String username = parsed.baseArgs().get(0);

                    var userOpt = rbacSystem.getUserManager().findByUsername(username);
                    if (userOpt.isEmpty()) {
                        System.out.println("User should exist");
                        return;
                    }

                    User user = userOpt.get();

                    Function<String, Optional<String>> getFlag = key ->
                            Optional.ofNullable(parsed.flags().get(key))
                                    .map(list -> list.get(0));

                    String newFullName = getFlag.apply("--fullname").orElse(user.fullname());
                    String newEmail = getFlag.apply("--email").orElse(user.email());

                    try {
                        rbacSystem.getUserManager().update(username, newFullName, newEmail);
                        System.out.println("User " + username + "update");
                    } catch (IllegalArgumentException e) {
                        System.out.println("error " + e.getMessage());
                    }
                })
        ));

        parser.register(new Command(
                "user-delete",
                "Удаление пользователя",
                ((scanner, rbacSystem, args) -> {

                    if (args.size() != 1 ) {
                        ArgumentError();
                        return;
                    }

                    String username = args.get(0);

                    var userOpt = rbacSystem.getUserManager().findByUsername(username);
                    if (userOpt.isEmpty()) {
                        System.out.println("User not found");
                        return;
                    }

                    User user = userOpt.get();

                    System.out.println("User " + user.username());
                    System.out.println("fullanme "+ user.fullname());
                    System.out.println("Email " + user.email());

                    var assignmnets = rbacSystem.getAssignmentManager().findByUser(user);
                    if (!assignmnets.isEmpty()) {
                        System.out.println("Current assignments " + assignmnets.size());
                        for (var a : assignmnets) {
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
                    for (var assignment : assignmnets) {
                        if (rbacSystem.getAssignmentManager().remove(assignment)) {
                            countRemove++;
                        }
                    }

                    rbacSystem.getUserManager().remove(user);
                    System.out.println("User '" + username + "' remove\n");
                    System.out.println("Assignment delete: " + countRemove);
                })
        ));


    }

    private static void registerRoleCommands(CommandParser parser) {

    }

    private static void registerAssignmentCommands(CommandParser parser) {

    }

    private static void registerPermissionCommands(CommandParser parser) {

    }

    private static void registerUtilityCommands(CommandParser parser) {

    }

    private static void ArgumentError() {
        System.out.println("Error: incorrect number of arguments");
    }
}
