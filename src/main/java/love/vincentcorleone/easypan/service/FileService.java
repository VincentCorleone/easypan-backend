package love.vincentcorleone.easypan.service;

import love.vincentcorleone.easypan.entity.po.Code2Path;
import love.vincentcorleone.easypan.entity.po.LargeFile;
import love.vincentcorleone.easypan.entity.po.User;
import love.vincentcorleone.easypan.entity.vo.FileVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    void upload(User user, String currentPath, MultipartFile file);

    List<FileVo> loadFiles(User nickName, String currentPath);


    String createDownloadCode(User user, String currentPath, String fileName);

    Code2Path downloadFile(String code);

    boolean uploadByChunks(User user, String currentPath, MultipartFile file, Integer chunkIndex, Integer chunks, String fileName, String md5);

    void newFolder(String nickName, String currentPath, String folderName);

    boolean checkMd5(User user, String md5, String currentPath, String fileName);

    LargeFile getLargeFileBy3(User user, String currentPath, String fileName);

    void delete(User user, String currentPath, String fileName);

    void rename(User user, String currentPath, String fileName, String newName);

    List<String> loadDirs(User user, String targetPath);

    void moveTo(User user, String currentPath, String fileName, String targetPath);
}
