package love.vincentcorleone.easypan.service;

import love.vincentcorleone.easypan.entity.vo.FileVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    void upload(String nickName, MultipartFile file);

    List<FileVo> loadFiles(String nickName, String currentPath);
}
