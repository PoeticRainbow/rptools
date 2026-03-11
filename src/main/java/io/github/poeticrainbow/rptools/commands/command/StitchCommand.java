package io.github.poeticrainbow.rptools.commands.command;

import io.github.poeticrainbow.rptools.commands.RegisteredCommands;
import io.github.poeticrainbow.rptools.commands.SimpleCommand;
import io.github.poeticrainbow.rptools.util.ColorUtil;
import io.github.poeticrainbow.rptools.util.FileUtil;
import io.github.poeticrainbow.rptools.util.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class StitchCommand {
    public static SimpleCommand get() {
        return RegisteredCommands.of("stitch", "dir [width]", StitchCommand::execute);
    }

    public static void execute(String[] args) {
        FileUtil.getFoldersInPath(args[0])
                .forEach(folder -> stitchImagesInFolder(folder, args.length == 2 ? Integer.parseInt(args[1]) : -1));
    }

    public static void stitchImagesInFolder(File folder, int atlasWidth) {
        System.out.println("Stitching files in folder " + folder.getName());
        var images = FileUtil.getFilesInPath(folder.toPath())
                             .stream()
                             .map(Image::new)
                             .sorted(FileUtil.IMAGE_NAME)
                             .toList();
        if (images.isEmpty()) {
            System.out.println("Folder " + folder.getName() + " had no valid images");
            return;
        }

        // number of sprites in grid, -1 means auto square
        var spriteCountX = atlasWidth == -1 ? (int) Math.ceil(Math.sqrt(images.size())) : atlasWidth;
        var spriteCountY = (int) Math.ceil((double) images.size() / spriteCountX);
        // Get the first image to prepare atlas size, all images should be equal size
        var spriteWidth = images.getFirst().getWidth();
        var spriteHeight = images.getFirst().getHeight();
        // Filter out any images that do not match size
        images = images.stream()
                       .filter(image -> image.getWidth() == spriteWidth && image.getHeight() == spriteHeight)
                       .toList();

        final var atlasImage = new BufferedImage(spriteCountX * spriteWidth, spriteCountY * spriteHeight, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < images.size(); i++) {
            var sprite = images.get(i);
            final var sheetX = i % atlasWidth; // scrolling left to right, wrapping at the edge
            final var sheetY = i / atlasWidth; // how many times the width fits in

            sprite.forEach((color, x, y) -> {
                atlasImage.setRGB(
                    sheetX * spriteWidth + x,
                    sheetY * spriteHeight + y,
                    sprite.getColor(x, y).getRGB()
                );
            });
        }

        Image.write(folder.toPath().getParent().resolve(folder.getName()), atlasImage);
    }

    public static void executeOld(String[] args) {
        var directory = new File(args[0]);
        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("The given file does not exist.");
            return;
        }
        var images = directory.listFiles(ColorUtil.PNG_FILTER);
        if (images == null) return;
        images = Arrays.stream(images).sorted((file1, file2) -> {
            var name1 = FileUtil.getActualFileName(file1);
            var name2 = FileUtil.getActualFileName(file2);
            try {
                var i1 = Integer.parseInt(name1);
                var i2 = Integer.parseInt(name2);
                return Integer.compare(i1, i2);
            } catch (NumberFormatException e) {
                return name1.compareTo(name2);
            }
        }).toArray(File[]::new);
        var atlasWidth = args.length == 2 ? Integer.parseInt(args[1]) : (int) Math.ceil(Math.sqrt(images.length));
        var atlasHeight = (int) Math.ceil((double) images.length / atlasWidth);
        var sprite1 = new Image(images[0]);
        var spriteWidth = sprite1.getWidth();
        var spriteHeight = sprite1.getHeight();
        var atlas = new BufferedImage(atlasWidth * spriteWidth, atlasHeight * spriteHeight, BufferedImage.TYPE_INT_ARGB);

        var index = 0;
        for (File image : images) {
            System.out.println("Reading " + image.getName());
            var sheetX = index % atlasWidth;
            var sheetY = index / atlasWidth; //how many lines, how many times the width fits in

            System.out.println("Index " + index + " @ " + sheetX + "," + sheetY);

            var sprite = new Image(image);
            if (sprite.getWidth() != spriteWidth || sprite.getHeight() != spriteHeight) continue;
            for (int x = 0; x < sprite.getWidth(); x++) {
                for (int y = 0; y < sprite.getHeight(); y++) {
                    atlas.setRGB(sheetX * spriteWidth + x, sheetY * spriteHeight + y, sprite.getColor(x, y).getRGB());
                }
            }
            index++;
        }

        var atlasFile = directory.toPath().getParent().resolve(directory.getName()) + ".png";
        try {
            ImageIO.write(atlas, "png", new File(atlasFile));
            System.out.println("Finished stitching the atlas.");
        } catch (IOException e) {
            System.out.println("Could not save the atlas.");
        }
    }

}
