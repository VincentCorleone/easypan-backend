package love.vincentcorleone.easypan.service.impl;

import jakarta.servlet.http.HttpSession;
import love.vincentcorleone.easypan.Constants;
import love.vincentcorleone.easypan.service.CaptchaService;
import org.springframework.stereotype.Service;

@Service
public class CaptchaServiceImpl implements CaptchaService {
    @Override
    public boolean validate(String capatcha, String type, HttpSession session) {
        String _capatcha;
        if("email".equals(type)){
            _capatcha = (String)session.getAttribute(Constants.CAPTCHA_KEY_EMAIL);
            session.removeAttribute(Constants.CAPTCHA_KEY_EMAIL);
        }else{
            _capatcha = (String)session.getAttribute(Constants.CAPTCHA_KEY);
            session.removeAttribute(Constants.CAPTCHA_KEY);
        }

        return _capatcha!=null && _capatcha.equals(capatcha);
    }
}
