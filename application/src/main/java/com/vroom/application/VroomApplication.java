package com.vroom.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@ComponentScan(basePackages = {
        "com.vroom.application",
        "com.vroom.shared",
        "com.vroom.security",
        "com.vroom.content",
        "com.vroom.media",
        "com.vroom.notification",
        "com.vroom.learning"
})
@EntityScan(basePackages = {
        "com.vroom.security.model.entity",
        "com.vroom.content.model.entity",
        "com.vroom.media.model.entity",
        "com.vroom.notification.model.entity",
        "com.vroom.learning.model.entity"
})
@EnableJpaRepositories(basePackages = {
        "com.vroom.security.repository",
        "com.vroom.content.repository",
        "com.vroom.media.repository",
        "com.vroom.notification.repository",
        "com.vroom.learning.repository"
})
public class VroomApplication {
    public static void main(String[] args) {
        SpringApplication.run(VroomApplication.class, args);
    }
}