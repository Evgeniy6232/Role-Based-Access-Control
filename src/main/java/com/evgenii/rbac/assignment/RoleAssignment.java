package com.evgenii.rbac.assignment;

import com.evgenii.rbac.model.AssignmentMetadata;
import com.evgenii.rbac.model.Role;
import com.evgenii.rbac.model.User;

public interface RoleAssignment {

    String assignmentId();
    User user();
    Role role();
    AssignmentMetadata metadata();
    boolean isActive();
    String assignmentType();
}