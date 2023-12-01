package love.vincentcorleone.easypan.service;

import love.vincentcorleone.easypan.entity.po.User;

public interface UserService {
    User findUserByEmail(String email);

    void register(String email, String nickname, String password);

    User login(String email, String password);

    void resetPassword(String email, String password);
}
