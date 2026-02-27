package com.evgenii.rbac.command;

import com.evgenii.rbac.system.RBACSystem;

import java.util.*;

public class CommandParser {

    private Map<String, Command> commands; //зарегистрированные команды
    private Map<String, String> commandDescriptions; //описание команд для справки

    public CommandParser() {
        this.commands = new HashMap<>();
        this.commandDescriptions = new HashMap<>();
    }

    public void registerCommand(String name, String description, Command command) {
        commands.put(name, command);
        commandDescriptions.put(name, description);
    }

    public void executeCommand(String commandName, Scanner scanner, RBACSystem system) {
        Command command = commands.get(commandName);
        if (command == null) {
            System.out.println("Unknown command" + commandName);
            System.out.println("Type 'Help' to see available commands");
            return;
        }
        command.execute(scanner, system);
    }

    public void printHelp() {
        System.out.println("---------- List cmd ---------- \n");

        List<String> sortedCommands = new ArrayList<>(commands.keySet());
        Collections.sort(sortedCommands);

        for (String cmd : sortedCommands) {
            System.out.printf(" # %s - %s\n", cmd, commandDescriptions.get(cmd));
        }
        System.out.println();
    }

    public void parseAndExecute(String input, Scanner scanner, RBACSystem system) {
        String command = input.trim().toLowerCase();

        if (command.isEmpty()) return;
        executeCommand(command, scanner, system);
    }

    public static void main(String[] args) {
        CommandParser parser = new CommandParser();

        parser.registerCommand("hello", "Prints hello message",
                (scanner, system) -> System.out.println("Hello!"));

        parser.printHelp();

        Scanner scanner = new Scanner(System.in);
        parser.parseAndExecute("hello", scanner, null);
    }

}
