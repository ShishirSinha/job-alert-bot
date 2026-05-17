package com.example.jobalertbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JobAlertBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(JobAlertBotApplication.class, args);
    }
}
