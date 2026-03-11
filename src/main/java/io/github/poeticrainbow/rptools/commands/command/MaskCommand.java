package io.github.poeticrainbow.rptools.commands.command;

import io.github.poeticrainbow.rptools.commands.RegisteredCommands;
import io.github.poeticrainbow.rptools.commands.SimpleCommand;
import io.github.poeticrainbow.rptools.util.FileUtil;
import io.github.poeticrainbow.rptools.util.Image;

import java.awt.*;

public class MaskCommand {
    public static SimpleCommand get() {
        return RegisteredCommands.of("mask", "<mask:file|directory> <target:file|directory>", MaskCommand::execute);
    }

    public static void execute(String[] args) {
        if (args.length < 2) return;
        final var masks = FileUtil.getAllFilesInPath(args[0]);
        final var targets = FileUtil.getAllFilesInPath(args[1]);

        masks.stream()
             .map(Image::new)
             .flatMap(mask -> targets.stream()
                                     .map(Image::new)
                                     .map(target -> new Image[]{mask, target}))
             .forEach(MaskCommand::maskTarget);
    }

    private static void maskTarget(Image[] images) {
        maskTarget(images[0], images[1]);
    }

    private static void maskTarget(Image mask, Image target) {
        var output = new Image(mask.file);

        output.forEach((color, x, y) -> {
            if (color.getAlpha() <= 0) return new Color(0, 0, 0, 0);
            return target.getColor(x % target.getWidth(), y % target.getHeight());
        });

        output.write(target.file.toPath().resolveSibling(target.getName() + "_" + mask.getName()));
    }
}
