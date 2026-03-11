package io.github.poeticrainbow.rptools.commands.command;

import io.github.poeticrainbow.rptools.commands.RegisteredCommands;
import io.github.poeticrainbow.rptools.commands.SimpleCommand;
import io.github.poeticrainbow.rptools.util.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class AtlasCommand {
    public static SimpleCommand get() {
        return RegisteredCommands.of("atlas", "image index", AtlasCommand::execute);
    }

    public static void execute(String[] args) {
        try {
            var atlas = new Image(new File(args[0]));
            var index = Files.readString(Path.of(args[1]));
            var exportPath = Path.of(atlas.getName().replace(".", "_") + "_export");
            var sprites = new ArrayList<Sprite>();

            for (String line : index.split("\n")) {
                if (line.isBlank()) continue;
                // todo fix the regex
                var pattern = Pattern.compile("([a-z0-9:_/]+)\\s+x=(\\d+)\\s+y=(\\d+)\\s+w=(\\d+)\\s+h=(\\d+)");
                var matcher = pattern.matcher(line);

                while (matcher.find()) {
                    sprites.add(Sprite.from(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5)));
                }
            }

            exportPath.toFile().mkdirs();

            var start = System.currentTimeMillis();
            sprites.parallelStream().forEach(sprite -> {
                var image = new BufferedImage(sprite.width, sprite.height, BufferedImage.TYPE_INT_ARGB);

                sprite.forEach((x, y, atlasX, atlasY) -> image.setRGB(
                    x, y, atlas.getColor(atlasX, atlasY)
                               .getRGB()
                ));

                try {
                    var path = sprite.getPath(exportPath);
                    System.out.println("Writing sprite " + sprite.id() + " to path " + path);

                    var file = path.toFile();
                    file.mkdirs();
                    ImageIO.write(image, "png", file);
                } catch (IOException ignored) {
                }
            });

            System.out.println("Completed in " + (System.currentTimeMillis() - start) + " ms");
        } catch (IOException e) {
            System.out.println("IOException");
            e.printStackTrace();
        }
    }

    @FunctionalInterface
    private interface SpriteConsumer {
        void consume(int x, int y, int atlasX, int atlasY);
    }

    private record Sprite(String id, int x, int y, int width, int height) {
        private static Sprite from(String id, String x, String y, String width, String height) {
            return new Sprite(id, Integer.parseInt(x), Integer.parseInt(y), Integer.parseInt(width), Integer.parseInt(height));
        }

        public void forEach(SpriteConsumer consumer) {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    consumer.consume(x, y, x + this.x, y + this.y);
                }
            }
        }

        public String getNamespace() {
            return id.substring(0, id.indexOf(":"));
        }

        public String getValue() {
            return id.substring(id.lastIndexOf(":") + 1);
        }

        public Path getPath(Path parent) {
            return parent.resolve(getNamespace()).resolve(getValue() + ".png");
        }
    }
}
