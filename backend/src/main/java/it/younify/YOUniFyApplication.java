package it.younify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class YOUniFyApplication {

    public static void main(String[] args) {
        SpringApplication.run(YOUniFyApplication.class, args);
    }
}
