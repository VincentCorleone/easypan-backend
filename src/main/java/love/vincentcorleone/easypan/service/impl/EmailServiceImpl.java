package love.vincentcorleone.easypan.service.impl;

import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import love.vincentcorleone.easypan.Constants;
import love.vincentcorleone.easypan.config.AppConfig;
import love.vincentcorleone.easypan.service.EmailService;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    @Resource
    private AppConfig appConfig;

    @Resource
    private JavaMailSender javaMailSender;
    public void sendEmailCode(String email, HttpSession session) {
        String code = RandomStringUtils.random(6,false,true);
        session.setAttribute(Constants.EMAIL_CODE_KEY,code);
        this.sendEmailCode(email,code);
    }

    private void sendEmailCode(String toEmail, String code){
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,true);
            helper.setFrom(appConfig.senderUserName);
            helper.setTo(toEmail);
            helper.setSubject("请验证您在Easypan的邮箱");
            helper.setText(String.format("您的验证码是%s。",code));
            javaMailSender.send(message);
        } catch (MessagingException e) {
            logger.error("邮件发送失败");
            throw new RuntimeException("邮件发送失败");
        }
    }
}
