package love.vincentcorleone.easypan.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import love.vincentcorleone.easypan.Constants;
import love.vincentcorleone.easypan.entity.po.User;
import love.vincentcorleone.easypan.exception.BusinessException;
import love.vincentcorleone.easypan.service.CaptchaService;
import love.vincentcorleone.easypan.service.EmailService;
import love.vincentcorleone.easypan.service.UserService;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Validated
public class UserController {

    @Resource
    private CaptchaService captchaService;

    @Resource
    private UserService userService;

    @Resource
    private EmailService emailService;

    @GetMapping("/sendEmailCodeForRegister")
    public ResponseEntity<Object> sendEmailCodeForRegister(HttpSession session, @RequestParam("email") @Email(message = "邮箱格式不正确") String email, @RequestParam("captcha")  @Pattern(regexp = "^[0-9a-z]{5}$",message = "图片验证码格式不正确，正确格式为5位密码或数字") String captcha){
        //1.如果已存在该1用户，返回错误；否则发送邮箱验证码
        if(!captchaService.validate(captcha,"email",session)){
            throw new BusinessException("验证码未通过验证");
        }

        //1.1 查找用户
        User user = userService.findUserByEmail(email);
        if(user == null){
            emailService.sendEmailCode(email,session);
            Map<String,String> result = new HashMap();
            result.put("message","邮件发送成功");
            return new ResponseEntity<>(result, HttpStatusCode.valueOf(200) );
        }else throw new BusinessException("此邮箱已被注册，请更换其他邮箱注册");

    }

    @GetMapping("/sendEmailCodeForResetPassword")
    public ResponseEntity<Object> sendEmailCodeForResetPassword(HttpSession session, @RequestParam("email") @Email(message = "邮箱格式不正确") String email, @RequestParam("captcha") @Pattern(regexp = "^[0-9a-z]{5}$",message = "图片验证码格式不正确，正确格式为5位密码或数字") String captcha){
        if(!captchaService.validate(captcha,"email",session)){
            throw new BusinessException("验证码未通过验证");
        }

        User user = userService.findUserByEmail(email);
        if(user != null){
            emailService.sendEmailCode(email,session);
            Map<String,String> result = new HashMap<>();
            result.put("message","邮件发送成功");
            return new ResponseEntity<>(result, HttpStatusCode.valueOf(200) );
        }else throw new BusinessException("此邮箱未被注册，无法重置密码");
    }


    @PostMapping("/user/register")
    public ResponseEntity<Object> register(HttpSession session,
                         @RequestParam("email") @Email(message = "邮箱格式不正确") String email,
                         @RequestParam("emailCode") @Pattern(regexp = "^[0-9]{6}$",message = "邮箱验证码格式不正确，正确格式为6位数字") String emailCode,
                         @RequestParam("nickname") String nickname, //todo sql防注入
                         @RequestParam("password") @Pattern(regexp = "^[0-9a-z]{8,12}$",message = "密码格式不正确，正确格式为8-12位密码或数字") String password,
                         @RequestParam("captcha") @Pattern(regexp = "^[0-9a-z]{5}$",message = "图片验证码格式不正确，正确格式为5位密码或数字") String captcha){
        if(!captchaService.validate(captcha,null,session)){
            throw new BusinessException("验证码未通过验证");
        }
        validateEmailCode(session, emailCode);
        userService.register(email, nickname, password);
        Map<String,String> result = new HashMap<>();
        result.put("message","注册成功");
        return new ResponseEntity<>(result,HttpStatusCode.valueOf(200));
    }

    @PostMapping("/user/login")
    public ResponseEntity<Object> login(HttpSession session,
                      @RequestParam("email") @Email(message = "邮箱格式不正确") String email,
                      @RequestParam("password") @Pattern(regexp = "^[0-9a-z]{8,12}$",message = "密码格式不正确，正确格式为8-12位密码或数字") String password,
                      @RequestParam("captcha") @Pattern(regexp = "^[0-9a-z]{5}$",message = "图片验证码格式不正确，正确格式为5位密码或数字") String captcha){
        if(!captchaService.validate(captcha,null,session)){
            throw new BusinessException("验证码未通过验证");
        }
        User loginUser = userService.login(email, password);
        if (loginUser==null){
            Map<String,String> result = new HashMap<>();
            result.put("message","用户名或密码错误");
            return new ResponseEntity<>(result,HttpStatusCode.valueOf(403));
        }else{
            session.setAttribute(Constants.LOGIN_USER_KEY,loginUser);
            Map<String,String> result = new HashMap<>();
            result.put("message","登录成功");
            return new ResponseEntity<>(result,HttpStatusCode.valueOf(200));
        }
    }

    @GetMapping("/user/logout")
    public ResponseEntity<Object> logout(HttpSession session){
        session.removeAttribute(Constants.LOGIN_USER_KEY);
        Map<String,String> result = new HashMap<>();
        result.put("message","退出成功");
        return new ResponseEntity<>(result,HttpStatusCode.valueOf(200));
    }

    @PostMapping("/user/resetPassword")
    public ResponseEntity<Object> resetPassword(HttpSession session,
                              @RequestParam("email") @Email(message = "邮箱格式不正确") String email,
                              @RequestParam("emailCode") @Pattern(regexp = "^[0-9]{6}$",message = "邮箱验证码格式不正确，正确格式为6位数字") String emailCode,
                              @RequestParam("password") @Pattern(regexp = "^[0-9a-z]{8,12}$",message = "密码格式不正确，正确格式为8-12位密码或数字") String password,
                              @RequestParam("captcha") @Pattern(regexp = "^[0-9a-z]{5}$",message = "图片验证码格式不正确，正确格式为5位密码或数字") String captcha){
        if(!captchaService.validate(captcha,null,session)){
            throw new BusinessException("验证码未通过验证");
        }
        validateEmailCode(session, emailCode);
        Map<String,String> result = new HashMap<>();
        result.put("message","重置密码成功");
        userService.resetPassword(email,password);
        return new ResponseEntity<>(result,HttpStatusCode.valueOf(200));
    }

    private static void validateEmailCode(HttpSession session, String emailCode) {
        String _emailCode = (String) session.getAttribute(Constants.EMAIL_CODE_KEY);
        session.removeAttribute(Constants.EMAIL_CODE_KEY);
        if(_emailCode==null || !_emailCode.equals(emailCode)){
            throw new BusinessException("邮箱验证码不正确");
        }
    }

}
