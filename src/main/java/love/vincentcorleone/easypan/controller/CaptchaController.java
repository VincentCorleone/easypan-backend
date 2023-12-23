package love.vincentcorleone.easypan.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import love.vincentcorleone.easypan.Constants;
import love.vincentcorleone.easypan.exception.ResponseResult;
import love.vincentcorleone.easypan.util.Captcha;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class CaptchaController {

    @RequestMapping("/captcha")
    public void captcha(HttpServletRequest req, HttpServletResponse resp, @RequestParam("type") @Nullable String type){
        String code = Captcha.generateText();
        byte[] image = Captcha.generateImage(code);
        HttpSession session = req.getSession();
        if("email".equals(type)){
            session.setAttribute(Constants.CAPTCHA_KEY_EMAIL,code);
        }else{
            session.setAttribute(Constants.CAPTCHA_KEY,code);
        }
        try {
            resp.getOutputStream().write(image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
