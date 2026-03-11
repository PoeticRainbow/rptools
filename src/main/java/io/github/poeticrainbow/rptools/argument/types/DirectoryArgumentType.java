package io.github.poeticrainbow.rptools.argument.types;

import io.github.poeticrainbow.rptools.argument.ArgumentType;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

public class DirectoryArgumentType implements ArgumentType<File> {
    @Override
    public boolean valid(String arg) {
        try {
            return Path.of(arg).toFile().isDirectory();
        } catch (InvalidPathException ignored) {
        }
        return false;
    }

    @Override
    public File get(String arg) {
        return Path.of(arg).toFile();
    }

    @Override
    public Class<File> typeParameterClass() {
        return File.class;
    }
}
