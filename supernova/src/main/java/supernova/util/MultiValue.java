package supernova.util;

import java.util.Objects;

public class MultiValue<V, V1> {

    private V first;
    private V1 second;

    public MultiValue(V first, V1 second) {
        this.first = first;
        this.second = second;
    }

    public V first() {
        return first;
    }

    public void setFirst(V first) {
        this.first = first;
    }

    public V1    second() {
        return second;
    }

    public void setSecond(V1 second) {
        this.second = second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MultiValue<?, ?> other)) return false;
        return Objects.equals(first, other.first) &&
                Objects.equals(second, other.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "{" + first + ", " + second + "}";
    }
}
