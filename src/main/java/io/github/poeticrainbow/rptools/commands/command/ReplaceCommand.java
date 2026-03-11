package io.github.poeticrainbow.rptools.commands.command;

import io.github.poeticrainbow.rptools.commands.RegisteredCommands;
import io.github.poeticrainbow.rptools.commands.SimpleCommand;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ReplaceCommand {
    public static SimpleCommand get() {
        return RegisteredCommands.of("replace", "file [target] replace", ReplaceCommand::execute);
    }

    public static void execute(String[] args) {
        var path = new File(args[0]);
        if (!path.exists()) {
            System.out.println("The given file does not exist.");
            return;
        }
        var files = path.isDirectory() ? path.listFiles((dir, name) -> name.endsWith(".json")) : new File[]{path};
        if (files == null) return;
        try {
            for (File file : files) {
                var content = Files.readString(file.toPath());
                var target = args.length < 3 ? "%s" : args[1];
                var replaceInput = args.length < 3 ? args[1] : args[2];
                var replaceWith = replaceInput.contains(",") ? replaceInput.split(",") : new String[]{replaceInput};
                for (String replace : replaceWith) {
                    var output = new File(file.toPath().toString().replace(target, replace));
                    output.getParentFile().mkdirs();

                    System.out.println(output);

                    var newContent = content.replace(target, replace);
                    Files.writeString(output.toPath(), newContent);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
