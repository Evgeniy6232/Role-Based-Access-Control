package com.evgenii.rbac.sorter;

import com.evgenii.rbac.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserSortersTest {

    private List<User> users;
    private User genna;
    private User misha;
    private User sasha;

    @BeforeEach
    void setUp() {
        genna = new User("genna", "Genna", "tekken1589556@gmail.com");
        misha = new User("misha", "Misha", "misha@mail.ru");
        sasha = new User("sasha", "Sasha", "sasha@gmail.com");

        users = new ArrayList<>();
        users.add(misha);
        users.add(sasha);
        users.add(genna);
    }

    @Test
    void byUsername() {
        users.sort(UserSorters.byUsername());

        assertEquals("genna", users.get(0).username());
        assertEquals("misha", users.get(1).username());
        assertEquals("sasha", users.get(2).username());
    }

    @Test
    void byFullName() {
        users.sort(UserSorters.byFullName());

        assertEquals("genna", users.get(0).fullname());
        assertEquals("misha", users.get(1).fullname());
        assertEquals("sasha", users.get(2).fullname());
    }

    @Test
    void byEmail() {
        users.sort(UserSorters.byEmail());

        assertEquals("misha@mail.ru", users.get(0).email());
        assertEquals("sasha@gmail.com", users.get(1).email());
        assertEquals("tekken1589556@gmail.com", users.get(2).email());
    }

    @Test
    void byUsername_reverseOrder() {
        users.sort(UserSorters.byUsername().reversed());

        assertEquals("sasha", users.get(0).username());
        assertEquals("misha", users.get(1).username());
        assertEquals("genna", users.get(2).username());
    }
}