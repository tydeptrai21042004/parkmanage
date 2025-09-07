package park.management.com.vn;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling; // <-- add this

@SpringBootApplication
@EnableScheduling
public class PartManagementApplicationRun implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication.run(PartManagementApplicationRun.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    // Startup hook (optional)
  }
}
