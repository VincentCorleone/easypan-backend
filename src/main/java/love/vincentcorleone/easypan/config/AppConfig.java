package love.vincentcorleone.easypan.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {

    @Value("${spring.mail.username:}")
    public String senderUserName;
}
