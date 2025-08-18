package supernova.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class Results<T, E> {

    private final T reference;
    private final List<E> violations;

    private Results(T reference, E violation) {
        this.reference = reference;

        this.violations = new ArrayList<>();
        if (violation != null) {
            this.violations.add(violation);
        }
    }

    private Results(T reference, List<E> violations) {
        this.reference = reference;
        this.violations = violations;
    }

    public static <T, E> Results<T, E> success(T reference) {
        return new Results<>(reference, null);
    }

    public static <T, E> Results<T, E> violation(E violation) {
        return new Results<>(null, violation);
    }

    public static <T, E> Results<T, E> violations(List<E> violations) {
        return new Results<>(null, violations);
    }

    public static <T, E> Validator<T, E> fromValidator(T reference, Class<E> violationClassType) {
        return new Validator<>(reference, violationClassType);
    }

    public boolean isSucceed() {
        return reference != null && violations.isEmpty();
    }

    public boolean hasViolations() {
        return !violations.isEmpty();
    }

    public T getReference() {
        return reference;
    }

    public List<E> getViolations() {
        return violations;
    }

    public Results<T, E> ifSuccess(Consumer<T> action) {
        if (isSucceed()) action.accept(reference);
        return this;
    }

    public Results<T, E> ifViolation(Consumer<List<E>> action) {
        if (hasViolations()) action.accept(violations);
        return this;
    }

    public <U> Results<U, E> map(Function<T, U> mapper) {
        if (isSucceed()) return Results.success(mapper.apply(reference));
        else return Results.violations(violations);
    }

    public <F> Results<T, F> mapViolation(Function<List<E>, F> mapper) {
        if (hasViolations()) return Results.violation(mapper.apply(violations));
        else return Results.success(reference);
    }

    public T orElse(T defaultValue) {
        return isSucceed() ? reference : defaultValue;
    }

    public void throwEachViolations() {
        for (E violation : violations) {
            if (violation instanceof Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }
    }

    /**
     * Validator is used for a combo in {@link Results}
     */
    public static class Validator<T, E> {

        private final T reference;
        private final List<E> violations;

        public Validator(T reference, Class<E> clazz) {
            this.reference = reference;
            this.violations = new ArrayList<>();
        }

        /**
         * This validation is to check {@link T} reference is not null.
         * Otherwise, it would add violation to the violation list.
         */
        public Validator<T, E> notNull(E violation) {
            if (reference == null) {
                this.violations.add(violation);
            }
            return this;
        }

        /**
         * This validation is to check {@link T} reference is null.
         * Otherwise, it would add violation to the violation list.
         */
        public Validator<T, E> isNull(E violation) {
            if (reference != null) {
                this.violations.add(violation);
            }
            return this;
        }

        public Validator<T, E> notEmpty(E violation) {
            switch (reference) {
                case null -> this.violations.add(violation);
                case String s when s.isEmpty() -> this.violations.add(violation);
                case Collection<?> c when c.isEmpty() -> this.violations.add(violation);
                case Map<?, ?> m when m.isEmpty() -> this.violations.add(violation);
                default -> {
                }
            }
            return this;
        }

        public Validator<T, E> isEmpty(E violation) {
            if (reference instanceof String s && !s.isEmpty()) {
                this.violations.add(violation);
            } else if (reference instanceof Collection<?> c && !c.isEmpty()) {
                this.violations.add(violation);
            } else if (reference instanceof Map<?,?> m && !m.isEmpty()) {
                this.violations.add(violation);
            }
            return this;
        }

        public <U> Validator<T, E> instanceOf(Class<U> clazzType, E violation) {
            if (!clazzType.isInstance(reference)) {
                this.violations.add(violation);
            }
            return this;
        }

        public <U> Validator<T, E> isEqualsTo(U obj, E violation) {
            if (!reference.equals(obj)) {
                this.violations.add(violation);
            }
            return this;
        }

        public <U> Validator<T, E> notEqualsTo(U obj, E violation) {
            if (reference.equals(obj)) {
                this.violations.add(violation);
            }
            return this;
        }

        public <U extends Comparable<U>> Validator<T, E> greaterThan(U other, E violation) {
            if (reference instanceof Comparable<?> c) {
                @SuppressWarnings("unchecked")
                U ref = (U) reference;
                if (ref.compareTo(other) <= 0) {
                    this.violations.add(violation);
                }
            } else {
                this.violations.add(violation);
            }
            return this;
        }

        public <U extends Comparable<U>> Validator<T, E> lessThan(U other, E violation) {
            if (reference instanceof Comparable<?> c) {
                @SuppressWarnings("unchecked")
                U ref = (U) reference;
                if (ref.compareTo(other) >= 0) {
                    this.violations.add(violation);
                }
            } else {
                this.violations.add(violation);
            }
            return this;
        }

        public Validator<T, E> minLength(int min, E violation) {
            if (reference == null) {
                this.violations.add(violation);
            }

            int length = -1;

            if (reference instanceof String str) {
                length = str.length();
            } else if (reference instanceof Collection<?> col) {
                length = col.size();
            } else if (reference instanceof Map<?, ?> map) {
                length = map.size();
            } else if (reference != null && reference.getClass().isArray()) {
                length = Array.getLength(reference);
            }

            if (length != -1 && length < min) {
                this.violations.add(violation);
            }

            return this;
        }

        public Validator<T, E> maxLength(int max, E violation) {
            if (reference == null) {
                this.violations.add(violation);
            }

            int length = -1;

            if (reference instanceof String str) {
                length = str.length();
            } else if (reference instanceof Collection<?> col) {
                length = col.size();
            } else if (reference instanceof Map<?, ?> map) {
                length = map.size();
            } else if (reference != null && reference.getClass().isArray()) {
                length = Array.getLength(reference);
            }

            if (length != -1 && length > max) {
                this.violations.add(violation);
            }

            return this;
        }

        public Results<T, E> toResults() {
            return new Results<>(reference, violations);
        }
    }
}
