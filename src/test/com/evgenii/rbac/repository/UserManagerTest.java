package com.evgenii.rbac.repository;

import com.evgenii.rbac.model.User;
import com.evgenii.rbac.filter.UserFilters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserManagerTest {

    private UserManager userManager;
    private User testUser;

    @BeforeEach
    void setUp() {
        userManager = new UserManager();
        testUser = new User("genna", "GennaQQ", "tekken1589556@gmail.com");
    }

    @Test
    void addUser_shouldSucceed() {
        userManager.add(testUser);

        assertEquals(1, userManager.count());
        assertTrue(userManager.exists("genna"));
    }

    @Test
    void addUser_withDuplicateUsername_shouldThrowException() {
        userManager.add(testUser);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userManager.add(new User("genna", "Another", "another@mail.com"));
        });

        assertTrue(exception.getMessage().contains("already exists"));
    }

    @Test
    void addUser_withNull_shouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userManager.add(null);
        });

        assertTrue(exception.getMessage().contains("User cannot be null"));
    }

    @Test
    void removeUser_shouldSucceed() {
        userManager.add(testUser);

        boolean removed = userManager.remove(testUser);

        assertTrue(removed);
        assertEquals(0, userManager.count());
    }

    @Test
    void removeUser_withNull_shouldReturnFalse() {
        assertFalse(userManager.remove(null));
    }

    @Test
    void findById_shouldReturnUser() {
        userManager.add(testUser);

        Optional<User> found = userManager.findById("genna");

        assertTrue(found.isPresent());
        assertEquals(testUser, found.get());
    }

    @Test
    void findById_withNonExistingId_shouldReturnEmpty() {
        Optional<User> found = userManager.findById("nonexistent");

        assertFalse(found.isPresent());
    }

    @Test
    void findByUsername_shouldReturnUser() {
        userManager.add(testUser);

        Optional<User> found = userManager.findByUsername("genna");

        assertTrue(found.isPresent());
        assertEquals(testUser, found.get());
    }

    @Test
    void findByEmail_shouldReturnUser() {
        userManager.add(testUser);

        Optional<User> found = userManager.findByEmail("tekken1589556@gmail.com");

        assertTrue(found.isPresent());
        assertEquals(testUser, found.get());
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        userManager.add(testUser);
        userManager.add(new User("genna2", "Genna Two", "genna2@mail.com"));

        List<User> users = userManager.findAll();

        assertEquals(2, users.size());
    }

    @Test
    void findByFilters_shouldReturnFilteredUsers() {
        userManager.add(testUser);
        userManager.add(new User("genna2", "Genna Two", "genna2@mail.com"));

        List<User> filtered = userManager.findByFilters(
                UserFilters.byUsernameContains("genna")
        );

        assertEquals(2, filtered.size());
    }

    @Test
    void update_shouldChangeUserData() {
        userManager.add(testUser);

        userManager.update("genna", "New Name", "new@mail.com");

        Optional<User> updated = userManager.findByUsername("genna");
        assertTrue(updated.isPresent());
        assertEquals("new name", updated.get().fullname());
        assertEquals("new@mail.com", updated.get().email());
    }

    @Test
    void update_withNonExistingUser_shouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userManager.update("nonexistent", "New Name", "new@mail.com");
        });

        assertTrue(exception.getMessage().contains("Username not found"));
    }
}