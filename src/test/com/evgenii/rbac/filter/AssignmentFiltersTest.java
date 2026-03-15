package com.evgenii.rbac.filter;

import com.evgenii.rbac.assignment.*;
import com.evgenii.rbac.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AssignmentFiltersTest {

    private User genna;
    private User misha;
    private Role admin;
    private Role user;
    private AssignmentMetadata meta1;
    private AssignmentMetadata meta2;
    private PermanentAssignment permAssign;
    private TemporaryAssignment tempAssign;
    private TemporaryAssignment expiredAssign;

    @BeforeEach
    void setUp() {
        genna = new User("genna", "GennaQQ", "tekken1589556@gmail.com");
        misha = new User("misha", "Misha", "misha@mail.ru");
        admin = new Role("ADMIN", "Administrator");
        user = new Role("USER", "Regular user");

        meta1 = AssignmentMetadata.now("admin", "Permanent assignment");
        meta2 = AssignmentMetadata.now("manager", "Temporary assignment");

        permAssign = new PermanentAssignment(genna, admin, meta1);
        tempAssign = new TemporaryAssignment(genna, user, meta2, "2026-12-31", false);
        expiredAssign = new TemporaryAssignment(misha, user, meta2, "2023-12-31", false);
    }

    @Test
    void byUser() {
        assertTrue(AssignmentFilters.byUser(genna).test(permAssign));
        assertTrue(AssignmentFilters.byUser(genna).test(tempAssign));
        assertFalse(AssignmentFilters.byUser(genna).test(expiredAssign));
        assertTrue(AssignmentFilters.byUser(misha).test(expiredAssign));
    }

    @Test
    void byUsername() {
        assertTrue(AssignmentFilters.byUsername("genna").test(permAssign));
        assertTrue(AssignmentFilters.byUsername("genna").test(tempAssign));
        assertFalse(AssignmentFilters.byUsername("genna").test(expiredAssign));
        assertTrue(AssignmentFilters.byUsername("misha").test(expiredAssign));
    }

    @Test
    void byRole() {
        assertTrue(AssignmentFilters.byRole(admin).test(permAssign));
        assertTrue(AssignmentFilters.byRole(user).test(tempAssign));
        assertTrue(AssignmentFilters.byRole(user).test(expiredAssign));
//        assertFalse(AssignmentFilters.byRole(admin).test(tempAssign));
    }

    @Test
    void byRoleName() {
        assertTrue(AssignmentFilters.byRoleName("ADMIN").test(permAssign));
        assertTrue(AssignmentFilters.byRoleName("USER").test(tempAssign));
        assertTrue(AssignmentFilters.byRoleName("USER").test(expiredAssign));
        assertFalse(AssignmentFilters.byRoleName("ADMIN").test(tempAssign));
    }

    @Test
    void activeOnly() {
        assertTrue(AssignmentFilters.activeOnly().test(permAssign));
        assertTrue(AssignmentFilters.activeOnly().test(tempAssign));
        assertFalse(AssignmentFilters.activeOnly().test(expiredAssign));
    }

    @Test
    void inactiveOnly() {
        assertFalse(AssignmentFilters.inactiveOnly().test(permAssign));
        assertFalse(AssignmentFilters.inactiveOnly().test(tempAssign));
        assertTrue(AssignmentFilters.inactiveOnly().test(expiredAssign));
    }

    @Test
    void byType() {
        assertTrue(AssignmentFilters.byType("PERMANENT").test(permAssign));
        assertTrue(AssignmentFilters.byType("TEMPORARY").test(tempAssign));
        assertTrue(AssignmentFilters.byType("TEMPORARY").test(expiredAssign));
        assertFalse(AssignmentFilters.byType("PERMANENT").test(tempAssign));
    }

    @Test
    void assignedBy() {
        assertTrue(AssignmentFilters.assignedBy("admin").test(permAssign));
        assertTrue(AssignmentFilters.assignedBy("manager").test(tempAssign));
        assertTrue(AssignmentFilters.assignedBy("manager").test(expiredAssign));
        assertFalse(AssignmentFilters.assignedBy("admin").test(tempAssign));
    }

    @Test
    void assignedAfter() {
        String date = "2025-12-31";
        assertTrue(AssignmentFilters.assignedAfter(date).test(permAssign));
        assertTrue(AssignmentFilters.assignedAfter(date).test(tempAssign));
        assertTrue(AssignmentFilters.assignedAfter(date).test(expiredAssign));
    }

    @Test
    void and_combineFilters() {
        AssignmentFilter isGenna = AssignmentFilters.byUsername("genna");
        AssignmentFilter isActive = AssignmentFilters.activeOnly();

        assertTrue(isGenna.and(isActive).test(permAssign));
        assertTrue(isGenna.and(isActive).test(tempAssign));
        assertFalse(isGenna.and(isActive).test(expiredAssign));
    }

    @Test
    void or_combineFilters() {
        AssignmentFilter isGenna = AssignmentFilters.byUsername("genna");
        AssignmentFilter isMisha = AssignmentFilters.byUsername("misha");

        assertTrue(isGenna.or(isMisha).test(permAssign));
        assertTrue(isGenna.or(isMisha).test(tempAssign));
        assertTrue(isGenna.or(isMisha).test(expiredAssign));
    }

    @Test
    void complexFilter() {
        AssignmentFilter filter = AssignmentFilters.byUsername("genna")
                .and(AssignmentFilters.byType("TEMPORARY"))
                .and(AssignmentFilters.activeOnly());

        assertFalse(filter.test(permAssign));
        assertTrue(filter.test(tempAssign));
        assertFalse(filter.test(expiredAssign));
    }
}