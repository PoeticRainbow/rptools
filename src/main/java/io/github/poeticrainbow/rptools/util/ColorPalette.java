package io.github.poeticrainbow.rptools.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static io.github.poeticrainbow.rptools.util.ColorUtil.hex;
import static io.github.poeticrainbow.rptools.util.ColorUtil.progress;

public class ColorPalette {
    private HashMap<Float, Color> valueToColor;

    private boolean normalized;
    private float minValue;
    private float maxValue;

    public ColorPalette() {
        this.valueToColor = new HashMap<>();
    }

    public ColorPalette(Image image) {
        this();
        image.createPalette().forEach(color -> valueToColor.put(ColorUtil.getAverage(color), color));
    }

    public Color closestColor(float value) {
        var closest = valueToColor.keySet()
                                  .stream()
                                  .min(Comparator.comparingDouble(o -> Math.abs(value - o)))
                                  .orElse(null);
        return valueToColor.get(closest);
    }

    public Color sampleColor(float value) {
        // closest value that is darker than the sample
        final var darkerColor = valueToColor.keySet().stream()
                                             .filter(v -> v < value)
                                             .min(Comparator.comparingDouble(v -> Math.abs(value - v)))
                                             .orElse(value);
        // closest value that is lighter than the sample
        final var lighterColor = valueToColor.keySet().stream()
                                              .filter(v -> v > value)
                                              .min(Comparator.comparingDouble(v -> Math.abs(value - v)))
                                              .orElse(value);

        // calculate a value to mix the colors by using value
        var mix = progress(value, darkerColor, lighterColor);
        return ColorUtil.mix(valueToColor.get(darkerColor), valueToColor.get(lighterColor), mix);
    }

    public Color normalizedSampleColor(Color color) {
        return sampleColor(getNormalizedValue(color));
    }

    public float getNormalizedValue(Color color) {
        if (!normalized) return ColorUtil.getAverage(color);
        return ColorUtil.progress(ColorUtil.getAverage(color), minValue, maxValue);
    }

    public Set<Color> colors() {
        return new HashSet<>(valueToColor.values());
    }

    public void normalize() {
        normalized = true;
        // normalize the values from 0 to 1
        var keySet = valueToColor.keySet();
        minValue = keySet.stream().min(Float::compareTo).orElse(0f);// - 0.05f;
        maxValue = keySet.stream().max(Float::compareTo).orElse(1f);// + 0.05f;
        System.out.println("    Darkest Value: " + minValue + " #" + hex(valueToColor.get(minValue)));
        System.out.println("    Lightest Value: " + maxValue + " #" + hex(valueToColor.get(maxValue)));
        final var newValueMap = new HashMap<Float, Color>();
        valueToColor.forEach((value, color) -> newValueMap.put(ColorUtil.progress(value, minValue, maxValue), color));
        valueToColor = newValueMap;
    }

    public BufferedImage createPaletteImage() {
        final var values = valueToColor.keySet().stream().sorted().toList();
        final var squaredSize = (int) Math.ceil(Math.sqrt(values.size()));
        final var output = new BufferedImage(squaredSize, squaredSize, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < values.size(); i++) {
            var value = values.get(i);
            var x = i % squaredSize;
            var y = i / squaredSize;
            output.setRGB(x, y, valueToColor.get(value).getRGB());
        }
        return output;
    }

    public BufferedImage createFullPaletteImage() {
        // Samples the palette at even intervals
        final var colors = 256;
        final var squaredSize = (int) Math.ceil(Math.sqrt(colors));
        final var output = new BufferedImage(squaredSize, squaredSize, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < colors; i++) {
            var x = i % squaredSize;
            var y = i / squaredSize;
            output.setRGB(x, y, sampleColor((float) i / 256).getRGB());
        }
        return output;
    }
}
