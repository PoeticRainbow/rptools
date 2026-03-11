package io.github.poeticrainbow.rptools.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Image {
    public File file;
    public BufferedImage original;
    public BufferedImage output;

    public BufferedImage original() {
        return original;
    }

    public BufferedImage output() {
        return output;
    }

    public Image(File file) {
        this.file = file;

        try {
            if (!file.exists() || file.isDirectory()) return;
            original = ImageIO.read(file);

            output = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        } catch (IOException e) {
            System.out.println("Error when creating Image from File.");
        }
    }

    public Image(BufferedImage image) {
        this.file = null;

        original = null;
        output = image;
    }

    public String getName() {
        var name = file.getName();
        return name.substring(0, name.lastIndexOf('.'));
    }

    public static void write(Path path, BufferedImage image) {
        var name = path.getFileName().toString();
        try {
            // add .png ending
            if (!name.endsWith(".png")) path = path.resolveSibling(name + ".png");
            var outputFile = new File(path.toUri());
            // make directories
            outputFile.getParentFile().mkdirs();
            System.out.println("Writing image to file...");
            ImageIO.write(image, "png", outputFile);
            System.out.println("Successfully wrote image to " + name);
        } catch (IOException e) {
            System.out.println("Error saving file " + name);
        }
    }

    public void write(Path path) {
        write(path, output);
    }

    public void write(String name) {
        write(Path.of(file.getParent() + File.separator + name), output);
    }

    public Set<Color> createPalette() {
        var colors = new HashSet<Color>();
        this.forEach(colors::add);
        return colors.stream()
                     .filter(color -> color.getAlpha() > 0)
                     .collect(Collectors.toSet());
    }

    public void writePalette() {
        try {
            System.out.println("Generating palette...");
            var colorList = createPalette().stream()
                                           .sorted(Comparator
                                               .comparing(ColorUtil.colorFunction, ColorUtil.compareHue)
                                               .thenComparing(ColorUtil.colorFunction, ColorUtil.compareSaturation)
                                               .thenComparing(ColorUtil.colorFunction, ColorUtil.compareValue))
                                           .toList();

            var image = new BufferedImage(colorList.size(), 1, BufferedImage.TYPE_INT_ARGB);
            for (int i = 0; i < colorList.size(); i++) {
                var color = colorList.get(i);
                image.setRGB(i, 0, color.getRGB());
            }

            ImageIO.write(image, "png", new File(file.getAbsolutePath() + "_palette.png"));
            System.out.println("Successfully wrote image to file.");
        } catch (IOException e) {
            System.out.println("Error saving file " + file.getName());
        }

    }

    public int getWidth() {
        return original.getWidth();
    }

    public int getHeight() {
        return original.getHeight();
    }

    public Color getColor(int x, int y) {
        // stupid grayscale color shenanigans
        if (original.getColorModel().getColorSpace().getType() == ColorSpace.TYPE_GRAY) {
            var dataObject = original.getRaster().getDataElements(x, y, null);
            if (dataObject instanceof byte[] pixelData) {
                var value = pixelData[0] & 0xFF;
                var alpha = pixelData.length < 2 ? 0xFF : pixelData[1] & 0xFF;
                return new Color(value, value, value, alpha);
            }
        }
        return new Color(original.getRGB(x, y), true);
    }

    public void setColor(Color color, int x, int y) {
        output.setRGB(x, y, color.getRGB());
    }

    public void forEach(Consumer<Color> consumer) {
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                consumer.accept(getColor(x, y));
            }
        }
    }

    public void forEach(ImageConsumer consumer) {
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                consumer.accept(getColor(x, y), x, y);
            }
        }
    }

    public void forEach(Function<Color, Color> function) {
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                var color = getColor(x, y);
                setColor(function.apply(color), x, y);
            }
        }
    }

    public void forEach(ImageFunction function) {
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                var color = getColor(x, y);
                setColor(function.apply(color, x, y), x, y);
            }
        }
    }

    @FunctionalInterface
    public interface ImageConsumer {
        void accept(Color color, int x, int y);
    }

    @FunctionalInterface
    public interface ImageFunction {
        Color apply(Color color, int x, int y);
    }
}
