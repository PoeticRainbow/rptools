package io.github.poeticrainbow.rptools.commands.command;

import io.github.poeticrainbow.rptools.commands.Command;
import io.github.poeticrainbow.rptools.commands.RegisteredCommands;
import io.github.poeticrainbow.rptools.util.ColorPalette;
import io.github.poeticrainbow.rptools.util.ColorUtil;
import io.github.poeticrainbow.rptools.util.Image;

import java.awt.*;
import java.io.File;
import java.util.Arrays;

import static io.github.poeticrainbow.rptools.util.ColorUtil.hex;

public class RecolorCommand {
    public static Command get() {
        return RegisteredCommands.of("recolor", "file|dir file|dir [--mkdir] [--print] [--noremap] [--noblend]", RecolorCommand::execute);
    }

    public static void execute(String[] args) {
        if (args.length < 2) System.out.println("Not enough parameters");
        final var listArgs = Arrays.asList(args);
        final var print = listArgs.contains("--print");
        final var remap = !listArgs.contains("--noremap");
        final var blend = !listArgs.contains("--noblend");
        final var mkdir = listArgs.contains("--mkdir");

        var samplePath = new File(args[0]);
        var targetPath = new File(args[1]);

        var samples = samplePath.isDirectory() ? samplePath.listFiles(ColorUtil.PNG_FILTER) : new File[]{samplePath};
        var targets = targetPath.isDirectory() ? targetPath.listFiles(ColorUtil.PNG_FILTER) : new File[]{targetPath};

        if (samples == null || targets == null) return;
        for (File sampleFile : samples) {
            for (File targetFile : targets) {
                if (sampleFile.isDirectory() || targetFile.isDirectory()) continue;

                System.out.println(sampleFile.getName() + " -> " + targetFile.getName());
                System.out.println("    Creating sample palette...");
                var sample = new Image(sampleFile);
                var samplePalette = new ColorPalette(sample);
                if (remap) {
                    System.out.println("    Normalizing sample palette...");
                    samplePalette.normalize();
                }

                System.out.println("    Creating target palette...");
                var target = new Image(targetFile);
                var targetPalette = new ColorPalette(target);
                if (remap) {
                    System.out.println("    Normalizing target palette...");
                    targetPalette.normalize();
                }

                System.out.println("    Recoloring image...");
                // perform the color transformation on each pixel in the target image
                target.forEach(color -> {
                    var value = targetPalette.getNormalizedValue(color);
                    var newColor = blend ? samplePalette.sampleColor(value) : samplePalette.closestColor(value);
                    if (newColor == null) return new Color(0, true);
                    if (print) System.out.println("    Value: " + value + ", " + hex(color) + " -> " + hex(newColor));
                    return new Color(newColor.getRed(), newColor.getGreen(), newColor.getBlue(), color.getAlpha());
                });

                System.out.println("    Writing new image...");
                target.write(sample.getName() + (mkdir ? File.separator : "_") + target.getName());
            }
        }
    }
}
