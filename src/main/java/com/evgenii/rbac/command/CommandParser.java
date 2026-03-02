package com.evgenii.rbac.command;

import com.evgenii.rbac.system.RBACSystem;
import java.util.*;

public class CommandParser {

    private final Map<String, Command> commands = new HashMap<>();

    public void register(Command command) {
        commands.put(command.name(), command);
    }

    public void execute(String input, Scanner scanner, RBACSystem system) {
        if (input == null || input.isBlank()) return;

        String[] parts = input.trim().split("\\s+");
        String commandName = parts[0];
        List<String> args = Arrays.asList(parts).subList(1, parts.length);

        Command command = commands.get(commandName);
        if (command == null) {
            System.out.println("Command dont found " + commandName);
            return;
        }

        command.execute(scanner, system, args);
    }

    public void printHelp() {
        System.out.println("----------Command list----------");
        for (Command cmd : commands.values()) {
            System.out.println("# " + cmd.name());
            System.out.println("  -" + cmd.description());
        }
    }

    public static Optional<ArgumentSet> parseArgs(List<String> args, Map<String, Integer> flagSignature) {
        Map<String, List<String>> flags = new HashMap<>();
        List<String> baseArgs = new ArrayList<>();

        int i = 0;
        while (i < args.size()) {
            String token = args.get(i);

            if (flagSignature.containsKey(token)) {
                int argCount = flagSignature.get(token);

                if (i + argCount >= args.size()) {
                    return Optional.empty();
                }

                List<String> values = args.subList(i + 1, i + 1 + argCount);
                flags.put(token, new ArrayList<>(values));

                i += argCount + 1;
            } else {
                baseArgs.add(token);
                i++;
            }
        }

        return Optional.of(new ArgumentSet(flags, baseArgs));
    }
}