package love.vincentcorleone.easypan.util;

import java.io.IOException;
import java.nio.file.*;
import java.io.File;

public class FileUtils {

    public static void deleteDirectory(Path path) throws IOException {
        if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
            try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
                for (Path entry : entries) {
                    deleteDirectory(entry);
                }
            }
        }

        Files.delete(path);
    }

    public static void moveFile(String sourcePath, String targetPath) {
        File sourceFile = new File(sourcePath);
        File targetFile = new File(targetPath);

        if (sourceFile.exists()) {
            boolean isMoved = sourceFile.renameTo(targetFile);

            if (!isMoved) {
                throw new RuntimeException("无法移动文件");
            }
        } else {
            throw new RuntimeException("源文件不存在");
        }
    }
}
