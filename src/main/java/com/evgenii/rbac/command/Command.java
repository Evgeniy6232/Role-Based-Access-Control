package com.evgenii.rbac.command;

import com.evgenii.rbac.system.RBACSystem;

import java.util.Scanner;

@FunctionalInterface
interface Command {
    void execute(Scanner scanner, RBACSystem system);
}