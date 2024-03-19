package love.vincentcorleone.easypan.service;

import love.vincentcorleone.easypan.entity.po.User;
import love.vincentcorleone.easypan.entity.vo.ShareVo;

public interface ShareService {
    ShareVo create(User user, String currentPath, String fileName, int validType, int howCode, String code);
}
