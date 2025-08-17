package supernova.event;

public class EventException extends RuntimeException {
    public EventException(String message) {
        super("Error while handling event: " + message);
    }
}
