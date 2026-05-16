package com.example.jobalertbot.scheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class SelfCallScheduler {

    @Value("${BASE_URL}")
    private String BASE_URL;

    @Scheduled(fixedRate = 60 * 1000)  // 10 minutes
    public void callHealthStatusEndpoint() throws IOException, InterruptedException {
        System.out.println("callHealthStatusEndpoint called");
        HttpClient httpClient = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(BASE_URL))
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
