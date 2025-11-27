package supernova.util;

/**
 * A generic wrapper representing a violation or error in an operation.
 *
 * <p>The {@code Violation} can hold any type of value, such as a message, exception, or custom object,
 * depending on the context of the operation.</p>
 *
 * @param <T> the type of the violation value
 * @author Izhar Atharzi
 * @since 0.0.1
 */
public record Violation<T>(T value) {
}