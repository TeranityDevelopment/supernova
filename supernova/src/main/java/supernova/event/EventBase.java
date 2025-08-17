package supernova.event;

import java.util.concurrent.Future;

public abstract class EventBase {

    private final boolean async;
    protected Future<?> future;

    public EventBase(boolean async) {
        this.async = async;
    }

    public EventBase() {
        this(false);
    }

    public boolean isAsync() {
        return async;
    }

    public boolean isCompleted() {
        return future != null && future.isDone();
    }
}