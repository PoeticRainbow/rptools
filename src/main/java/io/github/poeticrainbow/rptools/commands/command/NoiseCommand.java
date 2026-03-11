package io.github.poeticrainbow.rptools.commands.command;

import io.github.poeticrainbow.rptools.commands.RegisteredCommands;
import io.github.poeticrainbow.rptools.commands.SimpleCommand;

public class NoiseCommand {
    public static SimpleCommand get() {
        return RegisteredCommands.of("noise", "<mask:file|directory> <target:file|directory>", MaskCommand::execute);
    }

    public static void execute(String[] args) {
        // input noise_type width height x_scale y_scale tileable?


    }
}
