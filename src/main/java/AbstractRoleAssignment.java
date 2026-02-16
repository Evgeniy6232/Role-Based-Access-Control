import java.util.Objects;
import java.util.UUID;

public abstract class AbstractRoleAssignment implements RoleAssignment {

    private final String assignmentId;
    private final User user;
    private final Role role;
    private final AssignmentMetadata metadata;

    public AbstractRoleAssignment(User user, Role role, AssignmentMetadata metadata) {
        this.assignmentId = UUID.randomUUID().toString();
        this.user = user;
        this.role = role;
        this.metadata = metadata;
    }

    @Override
    public String assignmentId() {
        return assignmentId;
    }

    @Override
    public User user() {
        return user;
    }

    @Override
    public Role role() {
        return role;
    }

    @Override
    public AssignmentMetadata metadata() {
        return metadata;
    }

    @Override
    public abstract boolean isActive();

    @Override
    public abstract String assignmentType();

    @Override
    public boolean equals(Object comparable) {

        if (this == comparable) {
            return true;
        }

        if (comparable == null){
            return false;
        }

        if (getClass() != comparable.getClass()) {
            return false;
        }

        AbstractRoleAssignment comparableRoleAssignment = (AbstractRoleAssignment) comparable;
        return Objects.equals(assignmentId, comparableRoleAssignment.assignmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assignmentId);
    }

    public String summary() {

        StringBuilder resume = new StringBuilder();

        resume.append("[").append(assignmentType()).append("] ");
        resume.append(role().getName()).append(" assigned to ");
        resume.append(user().username()).append(" by ");
        resume.append(metadata().assignedBy()).append(" at ");
        resume.append(metadata().assignedAt()).append("\n Reason: ").append(metadata().reason());
        resume.append("\n Status: ").append(isActive() ? "ACTIVE" : "NOT ACTIVE");

        return resume.toString();
    }

    public static void main(String[] args) {
        User user = new User("john", "John", "john@test.com");
        Role role = new Role("ADMIN", "Admin");
        AssignmentMetadata meta = AssignmentMetadata.now("admin", "Test");

        AbstractRoleAssignment a = new AbstractRoleAssignment(user, role, meta) {
            public boolean isActive() { return true; }
            public String assignmentType() { return "PERMANENT"; }
        };

        System.out.println(a.summary());
    }
}