package io.github.poeticrainbow.rptools.commands.command;

import io.github.poeticrainbow.rptools.commands.RegisteredCommands;
import io.github.poeticrainbow.rptools.commands.SimpleCommand;
import io.github.poeticrainbow.rptools.util.ColorUtil;
import io.github.poeticrainbow.rptools.util.Image;

import java.io.File;

public class OverlayCommand {
    public static SimpleCommand get() {
        return RegisteredCommands.of("overlay", "<base:file|directory> <overlay:file>", OverlayCommand::execute);
    }

    public static void execute(String[] args) {
        final var basePath = new File(args[0]);
        final var overlayPath = new File(args[1]);
        if (!basePath.exists() || !overlayPath.exists()) {
            System.out.println("The given file(s) do not exist.");
            return;
        }
        final var bases = basePath.isDirectory() ? basePath.listFiles(ColorUtil.PNG_FILTER) : new File[]{basePath};
        if (bases == null) return;
        final var overlay = new Image(overlayPath);

        for (File baseFile : bases) {
            var base = new Image(baseFile);
            base.forEach((color, x, y) -> {
                var overlayColor = overlay.getColor(x % overlay.getWidth(), y % overlay.getHeight());
                if (overlayColor.getAlpha() >= color.getAlpha()) return overlayColor;
                return ColorUtil.mix(color, overlayColor, overlayColor.getAlpha()/255f);
            });

            base.write(base.getName());
        }
    }
}
