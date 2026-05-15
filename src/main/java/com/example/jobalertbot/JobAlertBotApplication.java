package com.example.jobalertbot;

import com.example.jobalertbot.config.EmailProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(EmailProperties.class)
public class JobAlertBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(JobAlertBotApplication.class, args);
    }
}
