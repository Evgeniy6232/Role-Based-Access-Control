package com.evgenii.rbac.filter;

import com.evgenii.rbac.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserFiltersTest {

    private User genna;
    private User misha;
    private User sasha;

    @BeforeEach
    void setUp() {
        genna = new User("genna", "GennaQQ", "tekken1589556@gmail.com");
        misha = new User("misha", "Misha", "misha@mail.ru");
        sasha = new User("sasha", "Sasha", "sasha@gmail.com");
    }

    @Test
    void byUsername_exactMatch() {
        assertTrue(UserFilters.byUsername("genna").test(genna));
        assertTrue(UserFilters.byUsername("misha").test(misha));
        assertTrue(UserFilters.byUsername("sasha").test(sasha));
        assertFalse(UserFilters.byUsername("genna").test(misha));
    }

    @Test
    void byUsernameContains() {
        assertTrue(UserFilters.byUsernameContains("gen").test(genna));
        assertTrue(UserFilters.byUsernameContains("mis").test(misha));
        assertTrue(UserFilters.byUsernameContains("sas").test(sasha));
        assertTrue(UserFilters.byUsernameContains("GEN").test(genna));
        assertFalse(UserFilters.byUsernameContains("xxx").test(genna));
    }

    @Test
    void byEmail_exactMatch() {
        assertTrue(UserFilters.byEmail("tekken1589556@gmail.com").test(genna));
        assertTrue(UserFilters.byEmail("misha@mail.ru").test(misha));
        assertTrue(UserFilters.byEmail("sasha@gmail.com").test(sasha));
        assertFalse(UserFilters.byEmail("wrong@mail.com").test(genna));
    }

    @Test
    void byEmailDomain() {
        assertTrue(UserFilters.byEmailDomain("@gmail.com").test(genna));
        assertTrue(UserFilters.byEmailDomain("@gmail.com").test(sasha));
        assertTrue(UserFilters.byEmailDomain("@mail.ru").test(misha));
        assertFalse(UserFilters.byEmailDomain("@yahoo.com").test(genna));
    }

    @Test
    void byFullNameContains() {
        assertTrue(UserFilters.byFullNameContains("Genna").test(genna));
        assertTrue(UserFilters.byFullNameContains("Misha").test(misha));
        assertTrue(UserFilters.byFullNameContains("Sasha").test(sasha));
        assertTrue(UserFilters.byFullNameContains("genna").test(genna));
        assertFalse(UserFilters.byFullNameContains("Petr").test(genna));
    }

    @Test
    void and_combineFilters() {
        UserFilter gmailFilter = UserFilters.byEmailDomain("@gmail.com");
        UserFilter nameContainsGen = UserFilters.byUsernameContains("gen");

        assertTrue(gmailFilter.and(nameContainsGen).test(genna));
        assertFalse(gmailFilter.and(nameContainsGen).test(misha));
        assertTrue(gmailFilter.and(UserFilters.byUsernameContains("sas")).test(sasha));
    }

    @Test
    void or_combineFilters() {
        UserFilter gmailFilter = UserFilters.byEmailDomain("@gmail.com");
        UserFilter mailruFilter = UserFilters.byEmailDomain("@mail.ru");

        assertTrue(gmailFilter.or(mailruFilter).test(genna));
        assertTrue(gmailFilter.or(mailruFilter).test(misha));
        assertTrue(gmailFilter.or(mailruFilter).test(sasha));

        UserFilter nameFilter = UserFilters.byUsernameContains("xxx");
        assertFalse(gmailFilter.and(nameFilter).test(genna));
    }
}