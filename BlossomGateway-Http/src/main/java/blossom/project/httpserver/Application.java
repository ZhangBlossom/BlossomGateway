package blossom.project.httpserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 *
 */
@SpringBootApplication(scanBasePackages = "blossom.project")
public class Application {


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
