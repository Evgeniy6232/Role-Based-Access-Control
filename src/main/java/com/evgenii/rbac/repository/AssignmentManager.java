package com.evgenii.rbac.repository;

import com.evgenii.rbac.assignment.*;
import com.evgenii.rbac.filter.*;
import com.evgenii.rbac.model.*;
import com.evgenii.rbac.sorter.*;
import com.evgenii.rbac.util.ValidationUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AssignmentManager implements Repository<RoleAssignment> {

    private ConcurrentHashMap<String, RoleAssignment> assignments = new ConcurrentHashMap<>();
    private UserManager userManager;
    private RoleManager roleManager;

    public AssignmentManager(UserManager userManager, RoleManager roleManager) {
        this.userManager = userManager;
        this.roleManager = roleManager;
    }

    private boolean isDuplicateActiveAssignment(RoleAssignment newAssignment) {
        for (RoleAssignment existing : assignments.values()) {
            if (existing.user().equals(newAssignment.user()) && existing.role().equals(newAssignment.role()) &&
                    existing.isActive()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void add(RoleAssignment assignment) {
        if (assignment == null) {
            throw new IllegalArgumentException("Assignment cannot be null");
        }

        ValidationUtils.requireNonEmpty(assignment.user().username(), "Username");
        ValidationUtils.requireNonEmpty(assignment.role().getName(), "Role name");

        if (!userManager.exists(assignment.user().username())) {
            throw new IllegalArgumentException("User does not exist");
        }

        if (!roleManager.exists(assignment.role().getName())) {
            throw new IllegalArgumentException("Role does not exist");
        }

        if (isDuplicateActiveAssignment(assignment)) {
            throw new IllegalArgumentException("User already has this active role");
        }

        assignments.put(assignment.assignmentId(), assignment);
    }

    @Override
    public boolean remove(RoleAssignment assignment) {
        if (assignment == null) {
            return false;
        }

        return assignments.remove(assignment.assignmentId()) != null;
    }

    @Override
    public Optional<RoleAssignment> findById(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }

        return Optional.ofNullable(assignments.get(id));
    }

    @Override
    public List<RoleAssignment> findAll() {
        return new ArrayList<>(assignments.values());
    }

    @Override
    public int count() {
        return assignments.size();
    }

    @Override
    public void clear() {
        assignments.clear();
    }

    public List<RoleAssignment> findByUser(User user) {
        List<RoleAssignment> result = new ArrayList<>();
        for (RoleAssignment a : assignments.values()) {
            if (a.user().equals(user)) {
                result.add(a);
            }
        }
        return result;
    }

    public List<RoleAssignment> findByRole(Role role) {
        List<RoleAssignment> result = new ArrayList<>();
        for (RoleAssignment a : assignments.values()) {
            if (a.role().equals(role)) {
                result.add(a);
            }
        }
        return result;
    }

    public List<RoleAssignment> findByFilter(AssignmentFilter filter) {
        List<RoleAssignment> result = new ArrayList<>();
        for (RoleAssignment a : assignments.values()) {
            if (filter.test(a)) {
                result.add(a);
            }
        }

        return result;
    }

    public List<RoleAssignment> findAll(AssignmentFilter filter, Comparator<RoleAssignment> sorter) {
        List<RoleAssignment> result = findByFilter(filter);
        result.sort(sorter);
        return result;
    }

    public List<RoleAssignment> getActiveAssignments() {
        return findByFilter(AssignmentFilters.activeOnly());
    }

    public List<RoleAssignment> getExpiredAssignments() {
        List<RoleAssignment> result = new ArrayList<>();
        for (RoleAssignment a : assignments.values()) {
            if (a instanceof TemporaryAssignment temp && temp.isExpired()) {
                result.add(a);
            }
        }
        return result;
    }

    public boolean userHasRole(User user, Role role) {
        for (RoleAssignment a : assignments.values()) {
            if (a.user().equals(user) && a.role().equals(role) && a.isActive()) {
                return true;
            }
        }

        return false;
    }

    public Set<Permission> getUserPermissions(User user) {
        Set<Permission> result = new HashSet<>();

        for (RoleAssignment assignment : assignments.values()) {
            if (assignment.user().equals(user) && assignment.isActive()) {
                result.addAll(assignment.role().getPermissions());
            }
        }

        return result;
    }

    public boolean userHasPermission(User user, String permissionName, String resource) {
        Set<Permission> permissions = getUserPermissions(user);
        for (Permission a : permissions) {
            if (a.name().equalsIgnoreCase(permissionName) && a.resource().equalsIgnoreCase(resource)) {
                return true;
            }
        }
        return false;
    }

    public void revokeAssignment(String assignmentId) {
        RoleAssignment assignment = assignments.get(assignmentId);

        if (assignment == null) {
            throw new IllegalArgumentException("Assignment not found" + assignmentId);
        }

        if (assignment instanceof PermanentAssignment perm) {
            perm.revoke();
            System.out.println("Perm assignment revoke");

        } else if (assignment instanceof TemporaryAssignment temp) {
            assignments.remove(assignmentId);
            System.out.println("Temp assignment removed");
        }
    }

    public void extendTemporaryAssignment(String assignmentId, String newExpirationDate) {
        RoleAssignment assignment = assignments.get(assignmentId);

        if (assignment == null) {
            throw new IllegalArgumentException("Assignment not found" + assignmentId);
        }

        if (assignment instanceof TemporaryAssignment temp) {
            temp.extend(newExpirationDate);
            IO.println("Assignment extend " + newExpirationDate);
        } else {
            throw new IllegalArgumentException("Assignment not a temporary");
        }

    }

    public static void main(String[] args) {
        UserManager um = new UserManager();
        RoleManager rm = new RoleManager();
        AssignmentManager am = new AssignmentManager(um, rm);

        User user = new User("genna", "genna", "genna@gmail.com");
        Role role = new Role("ADMIN", "Admin");
        um.add(user);
        rm.add(role);

        AssignmentMetadata meta = AssignmentMetadata.now("admin", "Test");
        PermanentAssignment pa = new PermanentAssignment(user, role, meta);

        am.add(pa);
        System.out.println(am.count());

        System.out.println(am.findByUser(user).size());

        System.out.println(am.userHasRole(user, role));

        am.revokeAssignment(pa.assignmentId());
        System.out.println(am.getActiveAssignments().size());
    }

}
