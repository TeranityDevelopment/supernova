package supernova.util;

import java.util.function.Consumer;
import java.util.function.Function;

public class Result<T, E> {

    private final T reference;
    private final E violation;

    private Result(T reference, E violation) {
        this.reference = reference;
        this.violation = violation;
    }

    public static <T, E> Result<T, E> success(T reference) {
        return new Result<>(reference, null);
    }

    public static <T, E> Result<T, E> violation(E violation) {
        return new Result<>(null, violation);
    }

    public boolean isSucceed() {
        return reference != null;
    }

    public boolean hasViolation() {
        return violation != null;
    }

    public T getReference() {
        return reference;
    }

    public E getViolation() {
        return violation;
    }

    public Result<T, E> ifSuccess(Consumer<T> action) {
        if (isSucceed()) action.accept(reference);
        return this;
    }

    public Result<T, E> ifViolation(Consumer<E> action) {
        if (hasViolation()) action.accept(violation);
        return this;
    }

    public <U> Result<U, E> map(Function<T, U> mapper) {
        if (isSucceed()) return Result.success(mapper.apply(reference));
        else return Result.violation(violation);
    }

    public <F> Result<T, F> mapViolation(Function<E, F> mapper) {
        if (hasViolation()) return Result.violation(mapper.apply(violation));
        else return Result.success(reference);
    }

    public T orElse(T defaultValue) {
        return isSucceed() ? reference : defaultValue;
    }

    public void throwIfViolation() {
        if (violation instanceof Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }
}
