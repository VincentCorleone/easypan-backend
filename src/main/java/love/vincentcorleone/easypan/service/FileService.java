package love.vincentcorleone.easypan.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    void upload(String nickName, MultipartFile file);
}
