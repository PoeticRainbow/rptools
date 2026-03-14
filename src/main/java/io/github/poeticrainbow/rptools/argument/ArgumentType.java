package io.github.poeticrainbow.rptools.argument;

public interface ArgumentType<T> {
    boolean valid(String arg);

    T get(String arg);

    Class<T> typeParameterClass();

    default String asString() {
        return "<" + typeParameterClass().getTypeName() + ">";
    }
}
