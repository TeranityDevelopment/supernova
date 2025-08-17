package supernova.test.event;

import supernova.event.Cancellable;
import supernova.event.EventBase;
import supernova.test.User;

public class UserCreatedEvent extends EventBase implements Cancellable {

    private boolean cancelled;
    private final User user;

    public UserCreatedEvent(User user) {
        super(true);
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
