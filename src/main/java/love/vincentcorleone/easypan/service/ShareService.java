package love.vincentcorleone.easypan.service;

import love.vincentcorleone.easypan.entity.po.User;
import love.vincentcorleone.easypan.entity.vo.ShareVo;
import love.vincentcorleone.easypan.entity.vo.ShareVoForGuest;

import java.util.HashMap;

public interface ShareService {
    ShareVo create(User user, String currentPath, String fileName, int validType);

    ShareVoForGuest info(String linkSuffix);

    HashMap<String, String> download(String linkSuffix);
}
