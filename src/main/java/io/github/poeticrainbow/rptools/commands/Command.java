package io.github.poeticrainbow.rptools.commands;

import io.github.poeticrainbow.rptools.argument.ArgumentType;

import java.util.List;

public class Command {
    private final String name;
    private final List<ArgumentType<?>> argumentTypes;

    public Command(String name, List<ArgumentType<?>> argumentTypes) {
        this.name = name;
        this.argumentTypes = argumentTypes;
    }

    public void execute(String[] args) {

    }

    public String usage() {
        var types = argumentTypes().stream()
                                   .map(ArgumentType::asString)
                                   .toArray(String[]::new);
        return name() + " " + String.join(" ", types);
    }

    public String name() {
        return name;
    }

    public List<ArgumentType<?>> argumentTypes() {
        return argumentTypes;
    }
}
