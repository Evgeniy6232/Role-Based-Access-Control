package com.evgenii.rbac.sorter;

import com.evgenii.rbac.assignment.*;

import java.util.Comparator;

public class AssignmentSorters {

    public static Comparator<RoleAssignment> byUserName() {
        return (a1, a2) -> a1.user().username().compareTo(a2.user().username());
    }

    public static Comparator<RoleAssignment> byRoleName() {
        return (a1, a2) -> a1.role().getName().compareTo(a2.role().getName());
    }

    public static Comparator<RoleAssignment> byAssignmentData() {
        return (a1, a2) -> a1.metadata().assignedAt().compareTo(a2.metadata().assignedAt());
    }
}
