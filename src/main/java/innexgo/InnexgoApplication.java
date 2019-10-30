package innexgo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableScheduling
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class InnexgoApplication {

  @Bean
  public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
      ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
      threadPoolTaskScheduler.setPoolSize(2); // We need 2 threads to execute the midnight sign out and the absence insertion
      return threadPoolTaskScheduler;
  }

  public static void main(String[] args) {
    SpringApplication.run(InnexgoApplication.class, args);
  }
}
