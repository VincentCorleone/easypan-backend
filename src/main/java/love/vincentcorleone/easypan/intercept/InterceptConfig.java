package love.vincentcorleone.easypan.intercept;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description: 将自定义拦截器添加到系统配置中，并设置拦截的规则
 * @Date 2023/2/13 13:13
 */
@Configuration
public class InterceptConfig implements WebMvcConfigurer {

    @Resource
    private LoginIntercept loginIntercept;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new LoginIntercept());//可以直接new 也可以属性注入
        registry.addInterceptor(loginIntercept).
                addPathPatterns("/**").    // 拦截所有 url
                excludePathPatterns("/api/user/login"). //不拦截登录注册接口
                excludePathPatterns("/api/user/register").
                excludePathPatterns("/api/user/resetPassword").
                excludePathPatterns("/api/sendEmailCodeForResetPassword").
                excludePathPatterns("/api/captcha").
                excludePathPatterns("/api/sendEmailCodeForRegister");
    }
}