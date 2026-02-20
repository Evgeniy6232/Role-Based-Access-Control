import java.security.Permissions;

public class RoleFilters {

    public static RoleFilter byName(String name) {
        return role -> role.getName().equals(name);
    }

    public static RoleFilter byNameContains(String substring) {
        return role -> role.getName().toLowerCase().contains(substring.toLowerCase());
    }

    public static RoleFilter hasPermission(Permission permission) {
        return role -> role.hasPermission(permission);
    }

    public static RoleFilter hasPermission(String permissionName, String resource) {
        return role -> role.hasPermission(permissionName, resource);
    }

    public static RoleFilter hasAtLeastNPermissions(int n) {
        return role -> role.getPermissions().size() >= n;
    }

    public static void main(String[] args) {
        Permission p1 = new Permission("READ", "users", "Read users");
        Permission p2 = new Permission("WRITE", "users", "Write users");

        Role admin = new Role("ADMIN", "Admin role");
        admin.addPermission(p1);
        admin.addPermission(p2);

        Role viewer = new Role("VIEWER", "Viewer role");
        viewer.addPermission(p1);

        RoleFilter f1 = RoleFilters.byName("ADMIN");
        System.out.println(f1.test(admin));
        System.out.println(f1.test(viewer));

        RoleFilter f2 = RoleFilters.hasAtLeastNPermissions(2);
        System.out.println(f2.test(admin));
        System.out.println(f2.test(viewer));

        RoleFilter f3 = RoleFilters.byNameContains("ADM")
                .and(RoleFilters.hasPermission("READ", "users"));
        System.out.println(f3.test(admin));
    }
}
