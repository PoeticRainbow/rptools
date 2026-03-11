package io.github.poeticrainbow.rptools.commands.command;

import io.github.poeticrainbow.rptools.commands.RegisteredCommands;
import io.github.poeticrainbow.rptools.commands.SimpleCommand;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class DuplicateCommand {
    public static SimpleCommand get() {
        return RegisteredCommands.of("duplicate", "file count", DuplicateCommand::execute);
    }

    public static void execute(String[] args) {
        var path = new File(args[0]);
        if (!path.exists()) {
            System.out.println("The given file does not exist.");
            return;
        }
        try {
            var content = Files.readString(path.toPath());
            var count = Integer.parseInt(args[1]);
            for (int i = 0; i < count; i++) {
                var newContent = content.replace("%s", String.valueOf(i + 1));
                Files.writeString(path.toPath().getParent().resolve(path.getName().replace("%s", String.valueOf(i + 1))), newContent);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
