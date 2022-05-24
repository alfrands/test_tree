package id.co.bni.mid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@ComponentScan(basePackages = "id.co.bni.mid")
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
