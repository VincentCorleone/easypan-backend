package love.vincentcorleone.easypan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@MapperScan("love.vincentcorleone.easypan.mapper")
public class EasypanApplication {

	public static ConfigurableApplicationContext ac;
	public static void main(String[] args) {
		EasypanApplication.ac = SpringApplication.run(EasypanApplication.class, args);
	}

}
