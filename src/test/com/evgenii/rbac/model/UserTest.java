package com.evgenii.rbac.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    void createUser_withValidData_shouldSucceed() {
        User user = new User("genna", "GennaQQ", "tekken1589556@gmail.com");

        assertEquals("genna", user.username());
        assertEquals("gennaqq", user.fullname());
        assertEquals("tekken1589556@gmail.com", user.email());
    }

    @Test
    void format_shouldReturnCorrectString() {
        User user = new User("genna", "GennaQQ", "tekken1589556@gmail.com");

        assertEquals("genna (gennaqq) <tekken1589556@gmail.com>", user.format());
    }

    @Test
    void createUser_withEmptyUsername_shouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new User("", "GennaQQ", "tekken1589556@gmail.com");
        });
        assertTrue(exception.getMessage().contains("Username"));
    }

    @Test
    void createUser_withEmptyFullname_shouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new User("genna", "", "tekken1589556@gmail.com");
        });
        assertTrue(exception.getMessage().contains("Full name"));
    }

    @Test
    void createUser_withEmptyEmail_shouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new User("genna", "GennaQQ", "");
        });
        assertTrue(exception.getMessage().contains("Email"));
    }

    @Test
    void createUser_withInvalidUsername_shouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new User("генна", "GennaQQ", "tekken1589556@gmail.com");
        });
        assertTrue(exception.getMessage().contains("Username"));
    }

    @Test
    void createUser_withShortUsername_shouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new User("ge", "GennaQQ", "tekken1589556@gmail.com");
        });
        assertTrue(exception.getMessage().contains("Username"));
    }

    @Test
    void createUser_withInvalidEmail_shouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new User("genna", "GennaQQ", "tekken1589556gmail.com");
        });
        assertTrue(exception.getMessage().contains("Email"));
    }
}