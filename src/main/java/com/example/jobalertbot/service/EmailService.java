package com.example.jobalertbot.service;

import com.example.jobalertbot.config.EmailProperties;
import com.example.jobalertbot.model.JobPosting;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Service
public class EmailService {

    private final WebClient webClient;
    private final EmailProperties emailProperties;

    @Value("${mailgun.api-key}")
    private String apiKey;

    @Value("${mailgun.domain}")
    private String domain;

    @Value("${mailgun.from}")
    private String from;

    public EmailService(WebClient webClient, EmailProperties emailProperties) {
        this.webClient = webClient;
        this.emailProperties = emailProperties;
    }

    public void sendJobAlert(List<JobPosting> jobs) {
        String subject = "Job Alert Bot - New Matching Jobs (" + jobs.size() + ")";
        String html = buildHtml(jobs);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("from", from);

        for (String recipient : emailProperties.getTo()) {
            form.add("to", recipient);
        }

        form.add("subject", subject);
        form.add("html", html);

        String auth = Base64.getEncoder()
                .encodeToString(("api:" + apiKey)
                        .getBytes(StandardCharsets.UTF_8));

        String response = webClient.post()
                .uri("https://api.mailgun.net/v3/" + domain + "/messages")
                .header("Authorization", "Basic " + auth)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(form)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        System.out.println("Email sent successfully. " + response);
    }

    private String buildHtml(List<JobPosting> jobs) {
        StringBuilder html = new StringBuilder();
        html.append("<h2>New Matching Jobs Found</h2>");
        html.append("<ul>");

        for (JobPosting job : jobs) {
            html.append("<li>")
                    .append("<b>").append(job.getTitle()).append("</b>")
                    .append(" - ").append(job.getCompany())
                    .append("<br/>")
                    .append(job.getLocation())
                    .append("<br/>")
                    .append("<a href='").append(job.getUrl()).append("'>Apply</a>")
                    .append("</li><br/>");
        }

        html.append("</ul>");
        return html.toString();
    }
}