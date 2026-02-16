public class PermanentAssignment extends AbstractRoleAssignment{

    private boolean revoked;

    public PermanentAssignment (User user, Role role, AssignmentMetadata metadata) {

        super(user, role, metadata);
        this.revoked = false;
    }

    @Override
    public boolean isActive() {
        return !revoked;
    }

    @Override
    public String assignmentType() {
        return "PERMANENT";
    }

    public void revoke() {
        this.revoked = true;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public static void main(String[] args) {
        User user = new User("genna", "Genna", "tekken1589556@gmail.com");
        Role role = new Role("ADMIN", "Admin");
        AssignmentMetadata meta = AssignmentMetadata.now("admin", null);

        PermanentAssignment test = new PermanentAssignment(user, role, meta);

        System.out.println(test.assignmentType());
        System.out.println(test.isActive());
    }
}