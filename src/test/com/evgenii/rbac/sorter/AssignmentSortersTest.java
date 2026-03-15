package com.evgenii.rbac.sorter;

import com.evgenii.rbac.assignment.*;
import com.evgenii.rbac.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AssignmentSortersTest {

    private List<RoleAssignment> assignments;
    private User genna;
    private User misha;
    private User sasha;
    private Role admin;
    private Role user;
    private Role manager;
    private AssignmentMetadata meta1;
    private AssignmentMetadata meta2;
    private AssignmentMetadata meta3;
    private PermanentAssignment pa1;
    private PermanentAssignment pa2;
    private TemporaryAssignment ta1;

    @BeforeEach
    void setUp() {
        genna = new User("genna", "Genna", "genna@mail.com");
        misha = new User("misha", "Misha", "misha@mail.ru");
        sasha = new User("sasha", "Sasha", "sasha@gmail.com");

        admin = new Role("ADMIN", "Administrator");
        user = new Role("USER", "Regular user");
        manager = new Role("MANAGER", "Manager");

        meta1 = AssignmentMetadata.now("admin", "First");
        meta2 = AssignmentMetadata.now("admin", "Second");
        meta3 = AssignmentMetadata.now("manager", "Third");

        pa1 = new PermanentAssignment(genna, admin, meta1);
        pa2 = new PermanentAssignment(misha, user, meta2);
        ta1 = new TemporaryAssignment(sasha, manager, meta3, "2026-12-31", false);

        assignments = new ArrayList<>();
        assignments.add(ta1);
        assignments.add(pa2);
        assignments.add(pa1);
    }

    @Test
    void byUserName() {
        assignments.sort(AssignmentSorters.byUserName());

        assertEquals("genna", assignments.get(0).user().username());
        assertEquals("misha", assignments.get(1).user().username());
        assertEquals("sasha", assignments.get(2).user().username());
    }

    @Test
    void byRoleName() {
        assignments.sort(AssignmentSorters.byRoleName());

        assertEquals("ADMIN", assignments.get(0).role().getName());
        assertEquals("MANAGER", assignments.get(1).role().getName());
        assertEquals("USER", assignments.get(2).role().getName());
    }

    @Test
    void byAssignmentData() {
        assignments.sort(AssignmentSorters.byAssignmentData());

        String date1 = assignments.get(0).metadata().assignedAt();
        String date2 = assignments.get(1).metadata().assignedAt();
        String date3 = assignments.get(2).metadata().assignedAt();

        assertTrue(date1.compareTo(date2) <= 0);
        assertTrue(date2.compareTo(date3) <= 0);
    }
}