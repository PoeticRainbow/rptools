package io.github.poeticrainbow.rptools.commands.command;

import io.github.poeticrainbow.rptools.commands.RegisteredCommands;
import io.github.poeticrainbow.rptools.commands.SimpleCommand;
import io.github.poeticrainbow.rptools.util.ColorUtil;
import io.github.poeticrainbow.rptools.util.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SplitCommand {
    public static SimpleCommand get() {
        return RegisteredCommands.of("split", "file|dir [sprite_size]", SplitCommand::execute);
    }

    public static void execute(String[] args) {
        var path = new File(args[0]);
        if (!path.exists()) {
            System.out.println("The given file does not exist.");
            return;
        }
        var files = path.isDirectory() ? path.listFiles(ColorUtil.PNG_FILTER) : new File[]{path};
        if (files == null) return;
        for (File file : files) {
            var atlas = new Image(file);
            var spriteSize = Integer.parseInt(args[1]);
            var sheetWidth = atlas.getWidth() / spriteSize;
            var sheetHeight = atlas.getHeight() / spriteSize;
            var index = 0;
            for (int sheetY = 0; sheetY < sheetHeight; sheetY++) {
                for (int sheetX = 0; sheetX < sheetWidth; sheetX++) {
                    var sprite = new BufferedImage(spriteSize, spriteSize, BufferedImage.TYPE_INT_ARGB);
                    for (int x = 0; x < spriteSize; x++) {
                        for (int y = 0; y < spriteSize; y++) {
                            sprite.setRGB(x, y, atlas.getColor(sheetX * spriteSize + x, sheetY * spriteSize + y).getRGB());
                        }
                    }

                    var output = file.toPath().getParent().resolve(
                        file.getName().split("\\.")[0] + File.separator + index + ".png"
                    ).toFile();
                    try {
                        output.getParentFile().mkdirs();
                        if (index == 0) System.out.print("Saving sprite ");
                        System.out.print(index + "...");
                        if (sheetX == 0) System.out.print("\n");
                        ImageIO.write(sprite, "png", output);
                    } catch (IOException e) {
                        System.out.println("Issue saving sprite " + index);
                    }
                    index++;
                }
            }
            System.out.println("Finished splitting the atlas.");
        }
    }
}
