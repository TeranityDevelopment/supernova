package supernova.test;

import supernova.event.EventManager;
import supernova.test.event.UserCreatedEvent;

public class User {

    private String name;
    private int age;

    public User(String name, int age) {
        this.name = name;
        this.age = age;

        // Initialize the event first
        UserCreatedEvent userCreatedEvent = new UserCreatedEvent(this);
        // Then fire the event before the rest of the logic
        // is actually executed (THIS IS IMPORTANT FOR CANCELLED EVENT)
        EventManager.fire(userCreatedEvent);

        if (userCreatedEvent.isCompleted()) {
            // Check if listener is cancelling the event
            if (!userCreatedEvent.isCancelled()) {
                // Do the logic
                System.out.println("User is created!");
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
