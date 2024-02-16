package com.telerikacademy.web.forumsystem;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Movie Forum", version = "1.0"))
public class ForumSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ForumSystemApplication.class, args);
    }

}
