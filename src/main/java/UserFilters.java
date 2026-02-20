public class UserFilters {

    public static UserFilter byUsername(String username) {
        return user -> user.username().equals(username);
    }

    public static UserFilter byUsernameContains(String substring) {
        return user -> user.username().toLowerCase().contains(substring.toLowerCase());
    }

    public static UserFilter byEmail(String email) {
        return user -> user.email().equals(email);
    }

    public static UserFilter byEmailDomain(String domain) {
        return user -> user.email().endsWith(domain);
    }

    public static UserFilter byFullNameContains(String substring) {
        return user -> user.fullname().toLowerCase().contains(substring.toLowerCase());
    }

    public static void main(String[] args) {
        UserFilter f1 = UserFilters.byUsernameContains("genna");
        UserFilter f2 = UserFilters.byEmailDomain("@gmail.com");

        UserFilter combined = f1.and(f2);

        User user = new User("Genna", "Genna qq", "tekken1589556@gmail.com");
        System.out.println(combined.test(user));
    }
}