package io.github.poeticrainbow.rptools.util;

import java.util.Objects;

public record Pair<F, S>(F first, S second) {
    @Override
    public String toString() {
        return "{ first: " + first + ", second: " + second + " }";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Pair<?, ?>(Object first1, Object second1))) return false;
        return Objects.equals(first1, first) && Objects.equals(second1, second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    public static <A, B> Pair<A, B> of(A a, B b) {
        return new Pair<>(a, b);
    }
}
