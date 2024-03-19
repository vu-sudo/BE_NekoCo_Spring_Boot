package vjames.developer.MessConnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MessConnectApplication {

	public static void main(String[] args) {
		SpringApplication.run(MessConnectApplication.class, args);
	}

}
