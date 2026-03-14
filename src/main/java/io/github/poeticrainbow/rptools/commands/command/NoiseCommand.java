package io.github.poeticrainbow.rptools.commands.command;

import de.articdive.jnoise.core.api.functions.Interpolation;
import de.articdive.jnoise.generators.noise_parameters.fade_functions.FadeFunction;
import de.articdive.jnoise.pipeline.JNoise;
import io.github.poeticrainbow.rptools.commands.RegisteredCommands;
import io.github.poeticrainbow.rptools.commands.SimpleCommand;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

public class NoiseCommand {
    public static SimpleCommand get() {
        return RegisteredCommands.of("noise", "<output:file> <width:int> <height:int> <scale:int>", NoiseCommand::execute);
    }

    public static void execute(String[] args) {
        // input noise_type width height x_scale y_scale tileable?
        System.out.println("args " + String.join(", ", args));
        var path = Path.of(args[0]);
        var width = Integer.parseInt(args[1]);
        var height = Integer.parseInt(args[2]);
        var scale = Double.parseDouble(args[3]);
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        var noise = JNoise.newBuilder()
                          .perlin(3302, Interpolation.CUBIC, FadeFunction.CUBIC_POLY)
                          .scale(1 / scale)
                          .addModifier(v -> (v + 1) / 2.0)
                          .clamp(0.0, 1.0)
                          .build();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                var sample = noise.evaluateNoise(x, y);
                System.out.printf("Noise value %s at %s, %s%n", sample, x, y);
                image.setRGB(x, y, new Color((float) sample, 0f, 0f).getRGB());
            }
        }

        try {
            ImageIO.write(image, "png", path.toFile());
        } catch (IOException ignored) {
        }
    }
}
