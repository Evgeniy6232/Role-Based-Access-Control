import java.util.*;

public class UserManager implements Repository<User> {

    private Map<String, User> users = new HashMap<>();

    @Override
    public void add(User user) {

        if (user == null) {
            throw new IllegalArgumentException("User cannot by null");
        }


        String username = user.username();
        if (users.containsKey(username)) {
            throw new IllegalArgumentException("User with username <" + username + "> already exists");
        }

        users.put(username, user);
    }

    @Override
    public boolean remove(User user) {
        if (user == null) {
            return false;
        }

        User removed = users.remove(user.username());
        return removed != null;
    }

    @Override
    public Optional<User> findById(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }

        User user = users.get(id);
        return Optional.ofNullable(user);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public int count() {
        return users.size();
    }

    @Override
    public void clear() {
        users.clear();
    }

    public Optional<User> findByUsername(String username) {
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(users.get(username));
    }

    public Optional<User> findByEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }

        for(User user : users.values()) {
            if (user.email().equals(email)){
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }

    public List<User> findByfilters(UserFilter filter) {
        List<User> result = new ArrayList<>();

        for (User user : users.values()) {
            if (filter.test(user)) {
                result.add(user);
            }
        }

        return result;
    }

    public List<User> findAll(UserFilter filter, Comparator<User> sorter) {

        List<User> result = findByfilters(filter);
        result.sort(sorter);

        return result;
    }

    public boolean exists(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }

        return users.containsKey(username);
    }

    public void update(String username, String newFullName, String newEmail) {

        User existing = users.get(username);
        if (username == null) {
            throw new IllegalArgumentException("Username not found " + username);
        }

        User updated = new User(username, newFullName, newEmail);
        
        users.put(username, updated);
    }
}
