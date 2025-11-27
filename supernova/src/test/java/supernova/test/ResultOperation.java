package supernova.test;

import supernova.util.Result;
import supernova.util.Violation;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class ResultOperation {

    private static final Map<String, User> users = new HashMap<>();

    public static void main(String[] args) {
        users.put("John Doe", new User("John Doe", 16));
    }

    public static Result<User> updateUser(User user, int age) {
        Result<User> result = Result.of();

        if (users.isEmpty()) {
            result.violate("Users is empty");
        }

        result.violateIf(!users.containsKey(user.getName()), "No such user with name: " + user.getName());
        result.violateIf(() -> user.getAge() == age, new IllegalStateException("Cannot set user to same age"));

        result.value(user);

        return result;
    }

    public static Result<User> getUser(String name) {
        // do something, fetch something, just literally do something.

        if (users.containsKey(name)) return Result.success(users.get(name));

        return Result.violation(new Violation<>(new NoSuchElementException("No user found with name: " + name)));
    }
}
