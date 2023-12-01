package love.vincentcorleone.easypan.service;

import jakarta.servlet.http.HttpSession;

public interface CaptchaService {
    public boolean validate(String captcha, String email, HttpSession session);
}
