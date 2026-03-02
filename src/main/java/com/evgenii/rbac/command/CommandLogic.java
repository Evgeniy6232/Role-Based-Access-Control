package com.evgenii.rbac.command;

import com.evgenii.rbac.system.RBACSystem;

import java.util.List;
import java.util.Scanner;


//Этот интерфейс будет служить полем класса CommandЮ когда парсер найдет команду
@FunctionalInterface
public interface CommandLogic {
    void execute(Scanner scanner, RBACSystem rbacSystem, List<String> args);
}
