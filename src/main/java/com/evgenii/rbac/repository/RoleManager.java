package com.evgenii.rbac.repository;

import com.evgenii.rbac.filter.RoleFilter;
import com.evgenii.rbac.model.Permission;
import com.evgenii.rbac.model.Role;
import com.evgenii.rbac.util.ValidationUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RoleManager implements Repository<Role> {

    private ConcurrentHashMap<String, Role> rolesById = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Role> rolesByName = new ConcurrentHashMap<>();

    @Override
    public synchronized void add(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }

        ValidationUtils.requireNonEmpty(role.getName(), "Role name");

        if (rolesByName.containsKey(role.getName())) {
            throw new IllegalArgumentException("Role with name <" + role.getName() + "> already exists");
        }

        rolesById.put(role.getId(), role);
        rolesByName.put(role.getName(), role);
    }

    @Override
    public synchronized boolean remove(Role role) {
        if (role == null) {
            return false;
        }

        Role removed = rolesById.remove(role.getId());
        rolesByName.remove(role.getName());

        return removed != null;
    }

    @Override
    public Optional<Role> findById(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }

        return Optional.ofNullable(rolesById.get(id));
    }

    public Optional<Role> findByName(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }

        return Optional.ofNullable(rolesByName.get(name));
    }

    @Override
    public List<Role> findAll() {
        return new ArrayList<>(rolesById.values());
    }

    @Override
    public int count() {
        return rolesById.size();
    }

    @Override
    public synchronized void clear() {
        rolesById.clear();
        rolesByName.clear();
    }

    public List<Role> findByFilter(RoleFilter filter) {
        List<Role> result = new ArrayList<>();

        for (Role role : rolesById.values()) {
            if (filter.test(role)) {
                result.add(role);
            }
        }

        return result;
    }

    public List<Role> findAll(RoleFilter filter, Comparator<Role> sorter) {
        List<Role> result = findByFilter(filter);
        result.sort(sorter);
        return result;
    }

    public boolean exists(String name) {
        return rolesByName.containsKey(name);
    }

    public void addPermissionToRole(String roleName, Permission permission) {
        Role role = rolesByName.get(roleName);
        if (role == null) {
            throw new IllegalArgumentException("com.evgenii.rbac.model.Role not found " + roleName);
        }

        role.addPermission(permission);
    }

    public void removePermissionFromRole(String roleName, Permission permission) {
        Role role = rolesByName.get(roleName);
        if (role == null) {
            throw new IllegalArgumentException("com.evgenii.rbac.model.Role not found " + roleName);
        }

        role.removePermission(permission);
    }

    public List<Role> findRolesWithPermission(String permissionName, String resource) {
        List<Role> result = new ArrayList<>();
        for (Role role : rolesById.values()) {
            if (role.hasPermission(permissionName, resource)) {
                result.add(role);
            }
        }

        return result;
    }

    public static void main(String[] args) {
        RoleManager manager = new RoleManager();

        Role admin = new Role("ADMIN", "Administrator");
        Role user = new Role("USER", "Regular user");

        manager.add(admin);
        manager.add(user);

        System.out.println(manager.count());

        Optional<Role> found = manager.findByName("ADMIN");
        System.out.println(found.isPresent());

        Permission p = new Permission("READ", "users", "Read users");
        manager.addPermissionToRole("ADMIN", p);

        List<Role> withPermission = manager.findRolesWithPermission("READ", "users");
        System.out.println(withPermission.size());
    }

    // Параллельная фильтрация ролей
    public List<Role> findByFilterParallel(RoleFilter filter) {
        if (filter == null) {
            return new ArrayList<>();
        }
        return rolesById.values().parallelStream()
                .filter(filter::test)
                .collect(Collectors.toList());
    }
}
