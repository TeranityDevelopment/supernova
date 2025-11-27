package supernova.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A container which may or may not contain a value, with also having violation(s).
 * When developers uses {@code of()} it will create  {@link Result} that you can add violations or value,
 * until {@link Result} stated as SUCCEED which you can state it to success by setting the value.
 * Otherwise, when developers use either {@code success()} or {@code violations{}},
 * it will create {@link Result} that stated either success or violated that you cannot set new
 * value or violate.
 *
 * <p>If {@code T} is present it means {@link Result} of an operation is succeeded,
 * otherwise, if {@link Result} have violation(s) it means operation is failed.
 *
 * <p>Success {@link Result} would require NO violations at all.
 *
 * <p>Violation(s) or also called error(s) can be {@link String} of error message, {@link Exception}, etc.
 *
 * @param <T> the type of the value
 * @author Izhar Atharzi
 * @since 0.0.1
 */
public class Result<T> {

    /**
     * The instance container that containing {@link Class} type of violation and
     * the violation value itself.
     */
    private final List<Violation<?>> violations;

    /**
     * The instance of the value reference.
     */
    private T value;

    /**
     * The default constructor of {@link Result}.
     *
     * @param value the type of the value reference
     */
    private Result(T value, List<Violation<?>> violations) {
        this.value = value;
        this.violations = violations;
    }

    /**
     * Returns {@link Result} containing the value.
     *
     * @param value the type of the value reference
     * @return An {@link Result} with {@code T} value
     * @param <T> the type of the value
     */
    public static <T> Result<T> success(T value) {
        return new Result<>(Objects.requireNonNull(value), Collections.emptyList());
    }

    /**
     * Returns {@link Result} containing a violation value.
     *
     * @param violation the type of violation value
     * @return An {@link Result} with violation
     * @param <T> the type of the value
     */
    public static <T> Result<T> violation(Violation<?> violation) {
        return new Result<>(null, List.of(violation));
    }

    public static <T> Result<T> violations(Violation<?>... violations) {
        return new Result<>(null, List.of(violations));
    }

    public static <T> Result<T> violations(List<Violation<?>> violations) {
        return new Result<>(null, List.copyOf(violations));
    }

    /**
     * Returns {@link Result} with containing {@link Class} type of the value.
     */
    public static <T> Result<T> of() {
        return new Result<>(null, new ArrayList<>());
    }

    /**
     * Checks if the {@link Result} is succeeded.
     * <p>
     * Returns {@code true} if reference is present, and returns {@code false} if violation is present
     */
    public boolean isSucceed() {
        return violations.isEmpty() && value != null;
    }

    /**
     * Checks if the {@link Result} is violation
     * <p>
     * Returns {@code true} if violation is present, and returns {@code false} is reference is present
     */
    public boolean hasViolation() {
        return !violations.isEmpty();
    }

    /**
     * Check if the {@link Result} containing specific violation that given to the param.
     *
     * @param violationValue the type of the violation value
     * @return Returns {@code true} if it's contains {@code E}, otherwise {@code false}
     */
    public boolean containsViolation(Object violationValue) {
        Objects.requireNonNull(violationValue);

        if (violations.isEmpty()) return false;

        for (Violation<?> violation : violations) {
            if (violation.value().equals(violationValue)) return true;
        }
        return false;
    }

    /**
     * Setter for {@code T} value.
     *
     * @param value the type of value
     */
    public void value(T value) {
        Objects.requireNonNull(value);

        if (this.value != null) throw new IllegalStateException("Cannot set new value for Result because Result already stated as successful");

        this.value = value;
    }

    /**
     * Set new value if the condition is true.
     */
    public void valueIf(boolean condition, T value) {
        Objects.requireNonNull(value);

        if (condition) {
            value(value);
        }
    }

    /**
     * Set new value if {@link Supplier} conditional check is true.
     */
    public void valueIf(Supplier<Boolean> condition, T value) {
        Objects.requireNonNull(value);
        Objects.requireNonNull(condition);

        valueIf(condition.get(), value);
    }

    /**
     * Violate the {@link Result} with {@code E} violation, which means the {@link Result} of the operation
     * is failed.
     *
     * @param violationValue the type of violation value
     */
    public <E> void violate(E violationValue) {
        Objects.requireNonNull(violationValue);

        for (Violation<?> violation : violations) {
            if (violation.value().equals(violationValue)) {
                return;
            }
        }

        if (value != null) {
            throw new IllegalStateException("Cannot violate Result because Result already stated as successful");
        }

        Violation<E> violation = new Violation<>(violationValue);
        violations.add(violation);
    }

    /**
     * Violate the {@link Result} with conditional check and {@code E} violation.
     *
     * @param condition the condition of the violation,
     *                  true - violation
     *                  false - not violation
     * @param violationValue the type of violation value
     */
    public <E> void violateIf(boolean condition, E violationValue) {
        Objects.requireNonNull(violationValue);

        if (condition) {
            violate(violationValue);
        }
    }

    /**
     * Violate the {@link Result} with conditional check of {@link Supplier} and {@code E} violation.
     *
     * @param condition the condition of the violation,
     *                  true - violation
     *                  false - not violation
     * @param violationValue the type of violation value
     */
    public <E> void violateIf(Supplier<Boolean> condition, E violationValue) {
        Objects.requireNonNull(violationValue);
        Objects.requireNonNull(condition);

        violateIf(condition.get(), violationValue);
    }

    /**
     * If {@link Result} is success then performs the given action with the reference, otherwise
     * perform nothing.
     */
    public void ifSuccess(Consumer<T> action) {
        if (isSucceed()) action.accept(value);
    }

    /**
     * If {@link Result} is failed then performs the given action with the violation, otherwise
     * perform nothing.
     */
    public void ifViolated(Consumer<List<Violation<?>>> action) {
        if (hasViolation()) action.accept(violations);
    }

    /**
     * If {@link Result} is success then returns {@link Stream} containing {@link T} reference.
     */
    public Stream<T> stream() {
        if (isSucceed()) {
            return Stream.of(value);
        } else {
            return Stream.empty();
        }
    }

    /**
     * If {@link Result} is failed then returns {@link Stream} containing violations.
     */
    public Stream<List<Violation<?>>> streamViolation() {
        if (!isSucceed()) {
            return Stream.of(violations);
        } else {
            return Stream.empty();
        }
    }

    /**
     * If the {@link Result} is success then returns {@code T}, otherwise violation(s).
     *
     * @return the value
     */
    public T get() {
        if (!isSucceed()) {
            for (Violation<?> violation : violations) {
                handleViolation(violation);
            }
        }
        return value;
    }

    /**
     * @return Returns immutable {@link List} of {@link Object} violation
     */
    public List<Violation<?>> getViolations() {
        return Collections.unmodifiableList(violations);
    }

    @SuppressWarnings("unchecked")
    private <V> void handleViolation(Violation<V> violation) {
        final ViolationHandler<V> violationHandler =
                (ViolationHandler<V>) ViolationHandlers.getViolationHandler(violation.value().getClass());

        if (violationHandler != null) {
            violationHandler.handle(violation);
        } else {
            System.out.println("Can't find violation handler for class type: " + violation.value().getClass());
        }
    }

    /**
     * Handles all violations.
     */
    public void handleViolations() {
        for (Violation<?> violation : violations) {
            handleViolation(violation);
        }
    }

    @Override
    public String toString() {
        return "Result{" +
                "violations=" + violations +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Result<?> result = (Result<?>) object;
        return Objects.equals(violations, result.violations) && Objects.equals(value, result.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(violations, value);
    }
}
