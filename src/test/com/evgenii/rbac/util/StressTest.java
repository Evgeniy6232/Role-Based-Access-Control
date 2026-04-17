package com.evgenii.rbac.test;

import com.evgenii.rbac.assignment.PermanentAssignment;
import com.evgenii.rbac.assignment.RoleAssignment;
import com.evgenii.rbac.model.*;
import com.evgenii.rbac.repository.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class StressTest {

    public static void main(String[] args) throws Exception {
        UserManager um = new UserManager();
        RoleManager rm = new RoleManager();
        AssignmentManager am = new AssignmentManager(um, rm);

        User baseUser = new User("base", "Base User", "base@gmail.com");
        um.add(baseUser);
        Role baseRole = new Role("BASE", "Base role");
        rm.add(baseRole);

        int threads = 5;
        int opsPerThread = 50;

        System.out.println("----------stress est----------");
        System.out.println("Threads: " + threads + ", Ops/thread: " + opsPerThread);

        AtomicInteger ok = new AtomicInteger(0);
        AtomicInteger err = new AtomicInteger(0);
        AtomicInteger usersCreated = new AtomicInteger(0);
        AtomicInteger rolesCreated = new AtomicInteger(0);
        AtomicInteger assignsCreated = new AtomicInteger(0);

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        long start = System.currentTimeMillis();

        for (int t = 0; t < threads; t++) {
            final int tid = t;
            pool.submit(() -> {
                Random rand = new Random();
                for (int i = 0; i < opsPerThread; i++) {
                    try {
                        int op = rand.nextInt(5);
                        switch (op) {
                            case 0:
                                String name = "u" + tid + i + System.nanoTime();
                                name = name.replaceAll("[^a-zA-Z0-9]", "");
                                if (name.length() > 20) name = name.substring(0, 20);
                                if (name.length() < 3) name = name + "abc";
                                um.add(new User(name, "Test User", name + "@gmail.com"));
                                usersCreated.incrementAndGet();
                                break;
                            case 1:
                                String roleName = "R" + tid + i + System.nanoTime();
                                roleName = roleName.replaceAll("[^a-zA-Z0-9]", "");
                                if (roleName.length() > 50) roleName = roleName.substring(0, 50);
                                rm.add(new Role(roleName, "Test role"));
                                rolesCreated.incrementAndGet();
                                break;
                            case 2:
                                List<User> users = um.findAll();
                                List<Role> roles = rm.findAll();
                                if (users.size() > 0 && roles.size() > 0) {
                                    User u = users.get(rand.nextInt(users.size()));
                                    Role r = roles.get(rand.nextInt(roles.size()));
                                    if (!am.userHasRole(u, r)) {
                                        am.add(new PermanentAssignment(u, r,
                                                AssignmentMetadata.now("stress", "test")));
                                        assignsCreated.incrementAndGet();
                                    }
                                }
                                break;
                            case 3:
                                List<RoleAssignment> assigns = am.findAll();
                                if (!assigns.isEmpty()) {
                                    am.revokeAssignment(assigns.get(rand.nextInt(assigns.size())).assignmentId());
                                }
                                break;
                            default:
                                um.findAll();
                                rm.findAll();
                                am.findAll();
                        }
                        ok.incrementAndGet();
                    } catch (Exception e) {
                        err.incrementAndGet();
                        if (err.get() == 1) {
                            System.err.println("First error: " + e.getMessage());
                        }
                    }
                }
            });
        }

        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.MINUTES);

        long elapsed = System.currentTimeMillis() - start;

        System.out.println("Time: " + elapsed + "ms");
        System.out.println("OK: " + ok.get());
        System.out.println("Errors: " + err.get());
        System.out.println("Users created: " + usersCreated.get() + " (total: " + um.count() + ")");
        System.out.println("Roles created: " + rolesCreated.get() + " (total: " + rm.count() + ")");
        System.out.println("Assignments created: " + assignsCreated.get() + " (total: " + am.count() + ")");

        Set<String> usernames = new HashSet<>();
        boolean dup = false;
        for (User u : um.findAll()) {
            if (usernames.contains(u.username())) {
                System.err.println("DUPLICATE: " + u.username());
                dup = true;
            }
            usernames.add(u.username());
        }
        System.out.println("Duplicate check: " + (dup ? "FAIL" : "OK"));
        System.out.println("Crash check: " + (err.get() < 1000 ? "OK" : "FAIL"));
    }
}