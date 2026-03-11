package io.github.poeticrainbow.rptools.commands;

import io.github.poeticrainbow.rptools.commands.command.*;
import io.github.poeticrainbow.rptools.util.ColorUtil;
import io.github.poeticrainbow.rptools.util.Image;

import java.awt.*;
import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class RegisteredCommands {
    public static final Map<String, Command> REGISTRY = new HashMap<>();

    public static Command RECOLOR = RecolorCommand.get();
    public static Command SPLIT = SplitCommand.get();
    public static Command STITCH = StitchCommand.get();
    public static Command OVERLAY = OverlayCommand.get();
    public static Command MASK = MaskCommand.get();
    public static Command DUPLICATE = DuplicateCommand.get();
    public static Command REPLACE = ReplaceCommand.get();
    public static Command ATLAS = AtlasCommand.get();
    public static Command FORMAT = of("format", "file", args -> {
        var image = new Image(new File(args[0]));
        var set = new HashSet<Color>();
        image.forEach(set::add);
        System.out.println("ColorModel:" + image.original.getColorModel());
        System.out.println("Type:" + image.original.getColorModel().getColorSpace().getType());
        System.out.println("Colors (" + set.size() + "):");
        var iterator = set.stream().sorted(Comparator.comparing(ColorUtil::getValue)).iterator();
        while (iterator.hasNext()) {
            System.out.print("#" + Integer.toHexString(iterator.next().getRGB()));
            if (iterator.hasNext()) System.out.print(", ");
        }
    });
    public static Command TEST = of("test", "[any]", args -> {
        System.out.println("You input: ");
        for (String arg : args) {
            System.out.print(arg + " ");
        }
    });
    public static Command LIST = ListCommand.get();
    public static Command NOISE = NoiseCommand.get();

    public static SimpleCommand of(String name, String usage, SimpleCommand.CommandAction action) {
        var registeredCommand = new SimpleCommand(name, usage, action);
        REGISTRY.put(name, registeredCommand);
        return registeredCommand;
    }

    public static void register() {}
}
