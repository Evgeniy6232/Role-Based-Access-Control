package com.evgenii.rbac.command;

import com.evgenii.rbac.system.RBACSystem;

import java.util.List;
import java.util.Scanner;

public record Command(String name, String description, CommandLogic logic) {
    public void execute(Scanner scanner, RBACSystem system, List<String> args) {
        logic.execute(scanner, system, args);
    }
}