package love.vincentcorleone.easypan;

import love.vincentcorleone.easypan.mapper.Code2PathMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 清理未下载的临时下载链接
 */
@Configuration
@EnableScheduling
public class SchedulerTask {

    @Autowired
    private Code2PathMapper code2PathMapper;

    @Scheduled(fixedRate = 1000 * 60 * 60 * 24)
    public void reportCurrentTime() {
        code2PathMapper.deleteAll();
    }

}