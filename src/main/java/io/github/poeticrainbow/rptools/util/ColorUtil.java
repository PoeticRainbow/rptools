package io.github.poeticrainbow.rptools.util;

import java.awt.*;
import java.io.FilenameFilter;
import java.util.Comparator;
import java.util.function.Function;

public class ColorUtil {
    public static final Function<Color, Color> colorFunction = color -> color;
    public static Comparator<Color> compareAverage = (color1, color2) -> {
        var value1 = (color1.getRed() + color1.getGreen() + color1.getBlue()) / 3;
        var value2 = (color2.getRed() + color2.getGreen() + color2.getBlue()) / 3;
        return Integer.compare(value1, value2);
    };
    public static final Comparator<Color> compareHue = (color1, color2) -> {
        var hue1 = getHueInt(color1);
        var hue2 = getHueInt(color2);
        if (Math.abs(hue2 - hue1) < 6) return 0;
        return Integer.compare(hue1, hue2);
    };
    public static final Comparator<Color> compareSaturation = (color1, color2) -> {
        var sat1 = getSaturationInt(color1);
        var sat2 = getSaturationInt(color2);
        if (Math.abs(sat2 - sat1) < 11) return 0;
        return Integer.compare(sat1, sat2);
    };
    public static final Comparator<Color> compareValue = (color1, color2) -> {
        var value1 = getValueInt(color1);
        var value2 = getValueInt(color2);
        return Integer.compare(value1, value2);
    };
    public static final FilenameFilter PNG_FILTER = (dir, name) -> name.endsWith(".png");

    public static String hex(Color color) {
        if (color == null) return "#null!";
        return "#" + Integer.toHexString(color.getRGB());
    }

    public static float progress(float value, float min, float max) {
        if (min == max) return 0.5f;
        return (value - min) / (max - min);
    }

    public static float progress(float value, Color darkerColor, Color lighterColor) {
        if (darkerColor == null) return 1f;
        if (lighterColor == null) return 0f;
        return progress(value, ColorUtil.getAverage(darkerColor), ColorUtil.getAverage(lighterColor));
    }

    public static Color mix(Color color1, Color color2, float x) {
        if (color1 == null || x == 1f) {
            return color2;
        }
        if (color2 == null || x == 0f) {
            return color1;
        }

        return new Color(
            lerpInt(color1.getRed(), color2.getRed(), x),
            lerpInt(color1.getGreen(), color2.getGreen(), x),
            lerpInt(color1.getBlue(), color2.getBlue(), x)
        );
    }

    public static int lerpInt(int min, int max, float x) {
        return Math.round(lerp(min, max, x));
    }

    public static float lerp(float min, float max, float x) {
        return min + (max - min) * x;
    }

    public static float getHue(Color color) {
        return colorToHSV(color)[0];
    }

    public static float getSaturation(Color color) {
        return colorToHSV(color)[1];
    }

    public static float getValue(Color color) {
        return colorToHSV(color)[2];
    }

    public static float getAverage(Color color) {
        return getAverageInt(color) / 255f;
    }

    public static int getHueInt(Color color) {
        return Math.round(getHue(color) * 255);
    }

    public static int getSaturationInt(Color color) {
        return Math.round(getSaturation(color) * 255);
    }

    public static int getValueInt(Color color) {
        return Math.round(getValue(color) * 255);
    }

    public static int getAverageInt(Color color) {
        return Math.round((color.getRed() + color.getBlue() + color.getGreen()) / 3f);
    }

    public static float[] colorToHSV(Color color) {
        return Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
    }
}
