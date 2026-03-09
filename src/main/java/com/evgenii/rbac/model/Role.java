package com.evgenii.rbac.model;
import com.evgenii.rbac.util.ValidationUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Role {

    private final String id;
    private final String name;
    private String description;
    private Set<Permission> permissions;


//    public Role(String name, String description) {
//
//        this.id = UUID.randomUUID().toString();
//
//        if (name == null || name.isBlank()) {
//            throw new IllegalArgumentException("com.evgenii.rbac.model.Role name cannot empty");
//        }
//
//        this.name = name;
//        this.description = description;
//        this.permissions = new HashSet<>();
//    }

    public Role(String name, String description) {
        ValidationUtils.requireNonEmpty(name, "Role name");

        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.permissions = new HashSet<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Permission> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }

    public void addPermission(Permission permission) {

        if (permission == null) {
            throw new IllegalArgumentException("com.evgenii.rbac.model.Permission cannot null");
        }
        permissions.add(permission);
    }

    public void removePermission(Permission permission) {
        permissions.remove(permission);
    }

    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }

    public boolean hasPermission(String permissionName, String resource) {

        for (Permission argument : permissions) {

            // Если имя совпадает с тем, что ищем, а также ресурс совпадает с тем, что ищем, то истина
            if (argument.name().equals(permissionName) && argument.resource().equals(resource)) {
                return true;
            }
        }

        return false;
    }

    //Есть сомнения в эффективности такого подхода
    @Override
    public boolean equals(Object comparable) {

        if (this == comparable) {
            return true;
        }

        if (comparable == null){
            return false;
        }

        if (getClass() == comparable.getClass()) {
            return true;
        }

        Role comparableRole = (Role) comparable;
        return id.equals(comparableRole.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "com.evgenii.rbac.model.Role{id: " + id + " name: " +  name + "}";
    }

    public String format() {

         StringBuilder recap = new StringBuilder();

         recap.append("com.evgenii.rbac.model.Role: ").append(name).append("[ID: ").append(id).append("]\n");
         recap.append("Description: ").append(description).append("\n");
         recap.append("com.evgenii.rbac.model.Permission (").append(permissions.size()).append("):");

         for (Permission argument : permissions) {
             recap.append("\n - ").append(argument.format());
         }

         return recap.toString();
    }

    public static void main(String[] args) {

        Permission p1 = new Permission("READ", "users", "Read users");
        Permission p2 = new Permission("WRITE", "users", "Write userrs");
        Permission p3 = new Permission("DELETE", "users", "Delete users");

        Role admin = new Role("ADMIN", "Admin role");

        admin.addPermission(p1);
        admin.addPermission(p2);
        admin.addPermission(p3);

        System.out.println(admin.hasPermission("READ", "users"));    // true
        System.out.println(admin.hasPermission("WRITE", "users"));   // true
        System.out.println(admin.hasPermission("DELETE", "users"));  // true
        System.out.println(admin.hasPermission("READ", "reports"));  // false

        admin.removePermission(p2);
        System.out.println(admin.hasPermission("WRITE", "users"));   // false

        System.out.println(admin.format());
    }
}