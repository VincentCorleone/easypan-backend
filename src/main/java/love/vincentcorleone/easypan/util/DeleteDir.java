package love.vincentcorleone.easypan.util;

import java.io.IOException;
import java.nio.file.*;

public class DeleteDir {

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
}
