package love.vincentcorleone.easypan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import love.vincentcorleone.easypan.entity.UserStatus;
import love.vincentcorleone.easypan.entity.po.User;
import love.vincentcorleone.easypan.mapper.UserMapper;
import love.vincentcorleone.easypan.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import java.util.Base64;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Override
    public User findUserByEmail(String email) {

        QueryWrapper<User> wrapper = new QueryWrapper<User>()
                .eq("email",email);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public void register(String email, String nickname, String password) {
        User user = new User();
        user.setEmail(email);
        user.setNickName(nickname);
        user.setPassword(hashPassword(password));
        user.setJoinTime(new Date());
        user.setStatus(UserStatus.Enabled.getCode());
        user.setUseSpace(0L);
        this.userMapper.insert(user);
    }

    @Override
    public User login(String email, String password) {
        User user = this.findUserByEmail(email);
        String _password = this.hashPassword(password);
        if (_password.equals(user.getPassword())){
            return user;
        }
        return null;
    }

    @Override
    public void resetPassword(String email, String password) {
        User user = this.findUserByEmail(email);
        if(user != null){
            user.setPassword(this.hashPassword(password));
            UpdateWrapper<User> wrapper = new UpdateWrapper<User>()
                    .eq("email",email);
            userMapper.update(user,wrapper);
        }else{
            throw new RuntimeException("该用户未注册");
        }
    }

    private String hashPassword(String password){
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(DigestUtils.md5Digest(password.getBytes()));
    }
}
