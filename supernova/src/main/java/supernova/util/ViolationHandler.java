package supernova.util;

/**
 * An abstract handler for processing individual violations of a specific type.
 *
 * <p>Users can extend this class to define custom behaviour for handling violations,
 * such as logging, throwing exceptions, or any other strategy.
 *
 * @param <T> the type of the violation value
 * @author Izhar
 * @since 0.0.1
 */
public abstract class ViolationHandler<T> {

    /**
     * Handle a single violation.
     *
     * @param violation the violation instance to process
     */
    public abstract void handle(Violation<T> violation);
}
