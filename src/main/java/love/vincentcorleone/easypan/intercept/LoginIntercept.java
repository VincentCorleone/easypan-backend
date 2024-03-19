package love.vincentcorleone.easypan.intercept;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import love.vincentcorleone.easypan.Constants;
import love.vincentcorleone.easypan.entity.po.User;
import love.vincentcorleone.easypan.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @Description: 自定义用户登录的拦截器
 * @Date 2023/2/13 13:06
 */
@Component
public class LoginIntercept implements HandlerInterceptor {
    // 返回 true 表示拦截判断通过，可以访问后面的接口
    // 返回 false 表示拦截未通过，直接返回结果给前端
    @Autowired
    private UserMapper userMapper;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        // 1.得到 HttpSession 对象
        HttpSession session = request.getSession(false);
        autoLoginWhenDevelop(session);
        if (session != null && session.getAttribute(Constants.LOGIN_USER_KEY) != null) {
            // 表示已经登录
            return true;
        }
        // 执行到此代码表示未登录，未登录就跳转到登录页面
        throw new RuntimeException("该页面需要登录才能访问");

    }

    private void autoLoginWhenDevelop(HttpSession session) {
        QueryWrapper<User> qw = new QueryWrapper<User>().eq("email","mr.vincent.ge@outlook.com");
        User user = userMapper.selectOne(qw);
        session.setAttribute(Constants.LOGIN_USER_KEY,user);
    }
}
