package com.example.jobalertbot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.email")
public class EmailProperties {

    private String from;
    private List<String> to;

    // Getters and setters
}