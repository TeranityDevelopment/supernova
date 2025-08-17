package supernova.test.event;

import supernova.event.EventListener;
import supernova.test.User;

public class EventTest {

    public static void main(String[] args) {
        User user = new User("izhar", 19);

        System.out.println(user);

        long start = System.nanoTime();

        // Test async
        for (int i = 0; i < 1000; i++) {
            for (int i1 = 0; i1 < 1000; i1++) {
                User user1 = new User("John Doe", 21);
            }
        }

        long end = System.nanoTime();
        long elapsed = end - start;

        System.out.println("Elapsed time: " + (elapsed / 1_000_000) + " ms");

    }

    @EventListener
    public void whenCreated(UserCreatedEvent event) {
        if (event.getUser().getAge() <= 19) {
            System.out.println("User under 19 are forbidden to be created.");
            event.setCancelled(true); // Cancel the rest of the user creation logic
        }
    }
}
