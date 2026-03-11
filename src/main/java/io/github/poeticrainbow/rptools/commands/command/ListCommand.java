package io.github.poeticrainbow.rptools.commands.command;

import io.github.poeticrainbow.rptools.commands.RegisteredCommands;
import io.github.poeticrainbow.rptools.commands.SimpleCommand;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;

public class ListCommand {
    public static SimpleCommand get() {
        return RegisteredCommands.of("list", "folder [file_type]", ListCommand::execute);
    }

    public static void execute(String[] args) {
        var path = Path.of(args[0]);
        var root = new File(args[0]);
        if (!root.exists()) {
            System.out.println("The given folder does not exist.");
            return;
        }
        if (root.isDirectory()) {
            try {
                try (var paths = Files.walk(path, FileVisitOption.FOLLOW_LINKS)) {
                    var builder = new StringBuilder();
                    var files = paths.map(Path::toFile)
                                     .filter(File::isFile)
                                     .map(file -> file.toPath().toString())
                                     .peek(s -> {
                                         builder.append(s);
                                         builder.append("\n");
                                     });

                    System.out.println("Found " + files.count() + " files matching criteria");
                    Toolkit.getDefaultToolkit()
                           .getSystemClipboard()
                           .setContents(new StringSelection(builder.toString()), null);
                    System.out.println("Copied list of files to clipboard");


                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //var fileType = args.length > 1 ? args[1] : "json";
            //var files = root.listFiles((dir, name) -> name.endsWith(fileType));
            //if (files == null) {
            //    System.out.println("No files were found.");
            //    return;
            //}
            //var builder = new StringBuilder();
            //var iterator = Arrays.stream(files).iterator();
            //while (iterator.hasNext()) {
            //    var name = iterator.next().getName();
            //    builder.append(name, 0, name.lastIndexOf("."));
            //    if (iterator.hasNext()) builder.append(",");
            //}
            //System.out.println(builder);
        }
    }
}
