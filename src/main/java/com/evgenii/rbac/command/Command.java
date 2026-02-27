package com.evgenii.rbac.command;

import java.util.Scanner;

@FunctionalInterface
interface Command {
    void execute(Scanner scanner, RBACSystem system);
}