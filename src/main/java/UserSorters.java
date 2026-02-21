import java.util.Comparator;

public class UserSorters {

    public static Comparator<User> byUsername() {
        return (u1, u2) -> u1.username().compareTo(u2.username());
    }

    public static Comparator<User> byFullName() {
        return (u1,u2) -> u1.fullname().compareTo(u2.fullname());
    }

    public static Comparator<User> byEmail() {
        return (u1, u2) -> u1.email().compareTo(u2.email());
    }

    public static void main(String[] args) {
        User u1 = new User("genna", "Genna qq", "genna@test.com");
        User u2 = new User("sanya", "Sanya bb", "sanya@test.com");

        System.out.println(UserSorters.byUsername().compare(u2, u1));
        System.out.println(UserSorters.byFullName().compare(u1, u2));
        System.out.println(UserSorters.byEmail().compare(u1, u2));
    }
}

