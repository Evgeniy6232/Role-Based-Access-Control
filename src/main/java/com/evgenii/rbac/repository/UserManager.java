package com.evgenii.rbac.repository;

import com.evgenii.rbac.model.User;
import com.evgenii.rbac.filter.*;
import com.evgenii.rbac.util.ValidationUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserManager implements Repository<User> {

    private ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();

    @Override
    public void add(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        ValidationUtils.requireNonEmpty(user.username(), "Username");
        ValidationUtils.requireNonEmpty(user.fullname(), "Full name");
        ValidationUtils.requireNonEmpty(user.email(), "Email");

        String username = user.username();
        if (users.containsKey(username)) {
            throw new IllegalArgumentException("User with username <" + username + "> already exists");
        }

        users.put(username, user);
    }

    @Override
    public boolean remove(User user) {
        if (user == null) {
            return false;
        }

        User removed = users.remove(user.username());
        return removed != null;
    }

    @Override
    public Optional<User> findById(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }

        User user = users.get(id);
        return Optional.ofNullable(user);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public int count() {
        return users.size();
    }

    @Override
    public void clear() {
        users.clear();
    }

    public Optional<User> findByUsername(String username) {
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(users.get(username));
    }

    public Optional<User> findByEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }

        for (User user : users.values()) {
            if (user.email().equals(email)) {
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }

    public List<User> findByFilters(UserFilter filter) {
        List<User> result = new ArrayList<>();

        for (User user : users.values()) {
            if (filter.test(user)) {
                result.add(user);
            }
        }

        return result;
    }

    public List<User> findAll(UserFilter filter, Comparator<User> sorter) {

        List<User> result = findByFilters(filter);
        result.sort(sorter);

        return result;
    }

    public boolean exists(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }

        return users.containsKey(username);
    }

    public void update(String username, String newFullName, String newEmail) {
        ValidationUtils.requireNonEmpty(username, "Username");
        ValidationUtils.requireNonEmpty(newFullName, "New full name");
        ValidationUtils.requireNonEmpty(newEmail, "New email");

        if (!users.containsKey(username)) {
            throw new IllegalArgumentException("Username not found: " + username);
        }

        User updated = new User(username, newFullName, newEmail);
        users.put(username, updated);
    }
}
