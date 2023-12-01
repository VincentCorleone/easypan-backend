package love.vincentcorleone.easypan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("love.vincentcorleone.easypan.mapper")
public class EasypanApplication {

	public static void main(String[] args) {
		SpringApplication.run(EasypanApplication.class, args);
	}

}
