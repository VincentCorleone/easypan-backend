package love.vincentcorleone.easypan.service;

import love.vincentcorleone.easypan.entity.vo.FileVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    void upload(String nickName, String currentPath, MultipartFile file);

    List<FileVo> loadFiles(String nickName, String currentPath);


    String createDownloadCode(String nickName, String currentPath, String fileName);

    String downloadFile(String code);

    boolean uploadByChunks(String nickName, String currentPath, MultipartFile file, Integer chunkIndex, Integer chunks, String fileName);

    void newFolder(String nickName, String currentPath, String folderName);
}
