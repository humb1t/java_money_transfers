package org.nipu.jmt;

import java.util.Objects;
import java.util.Optional;

/**
 * Composition of two possibilities:
 * 1) Operation succeed - then Value is presented.
 * 2) Operation failed - then Error is presented.
 *
 * @author Nikita_Puzankov
 */
public class Result<V, E> {
    private final V value;
    private final E error;

    public Result(V value, E error) {
        this.value = value;
        this.error = error;
    }

    public Optional<V> getValue() {
        return Optional.ofNullable(value);
    }

    public Optional<E> getError() {
        return Optional.ofNullable(error);
    }

    @Override
    public String toString() {
        return "{" +
                "'value'='" + value + "'" +
                ", 'error'='" + error + "'" +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Result<?, ?> result = (Result<?, ?>) o;
        return Objects.equals(value, result.value) &&
                Objects.equals(error, result.error);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, error);
    }
}
