package love.vincentcorleone.easypan.util;

import org.springframework.core.io.DefaultResourceLoader;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

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

    private static String getProjectPath(){
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
        URL url = resourceLoader.getClassLoader().getResource("");
        String projectPath = null;
        try {
            projectPath = url.toURI().getSchemeSpecificPart();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return projectPath;
    }

    public static String initUserRootDir(String nickName){
        String projectPath = getProjectPath();
        String basePath = projectPath + "files/";
        File dir = new File(basePath);
        if (!dir.exists()){
            dir.mkdirs();
        }

        basePath = basePath + nickName  ;
        dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return basePath;
    }

    public static String initUserAttachmentDir(String nickName){
        String projectPath = getProjectPath();
        String basePath = projectPath + "attachment/";
        File dir = new File(basePath);
        if (!dir.exists()){
            dir.mkdirs();
        }

        basePath = basePath + nickName  ;
        dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return basePath;
    }

    public static String initPublicFileDir(){
        String projectPath = getProjectPath();
        String basePath = projectPath + "files/";
        File dir = new File(basePath);
        if (!dir.exists()){
            dir.mkdirs();
        }

        basePath = basePath + "public/"  ;
        dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return basePath;
    }

    public static String initAttachmentPublicFileDir(){
        String projectPath = getProjectPath();
        String basePath = projectPath + "attachment/";
        File dir = new File(basePath);
        if (!dir.exists()){
            dir.mkdirs();
        }

        basePath = basePath + "public/"  ;
        dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return basePath;
    }
}
