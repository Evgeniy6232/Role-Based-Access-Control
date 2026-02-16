import java.time.LocalDateTime;

public record AssignmentMetadata(String assignedBy, String assignedAt, String reason) {

    public static AssignmentMetadata now(String assignedBy, String reason){

        String currentTime = LocalDateTime.now().toString();
        return new AssignmentMetadata(assignedBy, currentTime, reason);
    }

    public String format() {
        return String.format("Note: %s at %s Reason: %s", assignedBy, assignedAt, reason);
    }

    public static void main(String[] args) {

        AssignmentMetadata m1 = AssignmentMetadata.now("admin", "New");
        AssignmentMetadata m2 = AssignmentMetadata.now("manager", null);

        System.out.println(m1.format());
        System.out.println(m2.format());
    }
}