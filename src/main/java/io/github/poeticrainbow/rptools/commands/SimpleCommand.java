package io.github.poeticrainbow.rptools.commands;

import java.util.List;

public class SimpleCommand extends Command {
    private final CommandAction action;
    private final String usage;

    public SimpleCommand(String name, String usage, CommandAction action) {
        super(name, List.of());
        this.usage = usage;
        this.action = action;
    }

    @Override
    public void execute(String[] args) {
        action.execute(args);
    }

    @Override
    public String usage() {
        return usage;
    }

    @FunctionalInterface
    public interface CommandAction {
        void execute(String[] args);
    }
}
