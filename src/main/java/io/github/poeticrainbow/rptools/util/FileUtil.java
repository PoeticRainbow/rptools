package io.github.poeticrainbow.rptools.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

public class FileUtil {
    private static final Pattern STARTS_WITH_NUMBER = Pattern.compile("^(\\d+)");
    public static final Comparator<String> NUMERICAL_THEN_ALPHABETICAL = (string1, string2) -> {
        try {
            var int1 = STARTS_WITH_NUMBER.matcher(string1).group(1);
            var int2 = STARTS_WITH_NUMBER.matcher(string2).group(1);
            var i1 = Integer.parseInt(int1);
            var i2 = Integer.parseInt(int2);
            var result = Integer.compare(i1, i2);
            if (result != 0) return result;
            var name1 = string1.substring(string1.lastIndexOf(int1) + int1.length());
            var name2 = string2.substring(string2.lastIndexOf(int2) + int2.length());
            return name1.compareTo(name2);
        } catch (NumberFormatException | IllegalStateException ignored) {
        }
        return string1.compareTo(string2);
    };
    public static final Comparator<File> FILE_NAME = (file1, file2) -> NUMERICAL_THEN_ALPHABETICAL.compare(file1.getName(), file2.getName());
    public static final Comparator<Image> IMAGE_NAME = (image1, image2) -> NUMERICAL_THEN_ALPHABETICAL.compare(image1.getName(), image2.getName());

    public static boolean isPng(Path path) {
        return path.toString().endsWith(".png");
    }

    public static List<File> getFoldersInPath(String path) {
        return getFoldersInPath(Path.of(path));
    }

    public static List<File> getFoldersInPath(Path path) {
        try (var stream = Files.walk(path)) {
            return stream.map(Path::toFile)
                         .filter(File::isDirectory)
                         .distinct()
                         .toList();
        } catch (IOException ignored) {
        }
        return List.of();
    }

    public static List<File> getAllFilesInPath(String path) {
        return getAllFilesInPath(Path.of(path));
    }

    public static List<File> getAllFilesInPath(Path path) {
        try (var stream = Files.walk(path)) {
            return stream.filter(FileUtil::isPng)
                         .map(Path::toFile)
                         .filter(File::exists)
                         .toList();
        } catch (IOException ignored) {
        }
        return List.of();
    }

    public static List<File> getFilesInPath(Path path) {
        try (var stream = Files.walk(path, 1)) {
            return stream.filter(FileUtil::isPng)
                         .map(Path::toFile)
                         .filter(File::exists)
                         .filter(file -> !file.isDirectory())
                         .peek(file -> System.out.println(file.getPath()))
                         .toList();
        } catch (IOException ignored) {
        }
        return List.of();
    }

    public static String getActualFileName(File file) {
        return file.getName().substring(0, file.getName().lastIndexOf('.'));
    }
}
