package love.vincentcorleone.easypan.service;

import love.vincentcorleone.easypan.entity.po.User;

public interface UserService {
    User findUserByEmail(String email);

    User findUserById(Long id);

    void register(String email, String nickname, String password);

    User login(String email, String password);

    void resetPassword(String email, String password);

    void updatePwd(User user, String password);
}
