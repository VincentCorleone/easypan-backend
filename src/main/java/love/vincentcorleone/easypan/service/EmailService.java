package love.vincentcorleone.easypan.service;

import jakarta.servlet.http.HttpSession;

public interface EmailService {

    public void sendEmailCode(String email, HttpSession session);
}
