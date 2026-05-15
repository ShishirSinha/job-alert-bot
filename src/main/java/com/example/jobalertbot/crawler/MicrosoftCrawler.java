package com.example.jobalertbot.crawler;

import com.example.jobalertbot.model.JobPosting;
import com.example.jobalertbot.service.JobSearchCriteria;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

@Component
public class MicrosoftCrawler implements CompanyCrawler {

    private static final String API_URL =
            "https://apply.careers.microsoft.com/api/pcsx/search" +
                    "?domain=microsoft.com" +
                    "&query=" +
                    "&location=india" +
                    "&start=0" +
                    "&sort_by=timestamp" +
                    "&filter_include_remote=1" +
                    "&filter_career_discipline=Software+Engineering" +
                    "&filter_employment_type=full-time" +
                    "&filter_roletype=individual+contributor" +
                    "&filter_profession=software+engineering";

    private final WebClient webClient;

    public MicrosoftCrawler(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public String getCompanyName() {
        return "Microsoft";
    }

    @Override
    public List<JobPosting> fetchJobs(JobSearchCriteria criteria) {
        try {
            MicrosoftSearchResponse response = webClient.get()
                    .uri(API_URL)
                    .header("Accept", "application/json")
                    .retrieve()
                    .bodyToMono(MicrosoftSearchResponse.class)
                    .block();

            if (response == null ||
                    response.getData() == null ||
                    response.getData().getPositions() == null) {
                return Collections.emptyList();
            }

            return response.getData()
                    .getPositions()
                    .stream()
                    .map(this::toJobPosting)
                    .toList();

        } catch (Exception e) {
            System.err.println("Failed to fetch jobs from Microsoft: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private JobPosting toJobPosting(PositionDto dto) {
        JobPosting job = new JobPosting();

        job.setCompany(getCompanyName());
        job.setExternalJobId(String.valueOf(dto.getId()));
        job.setTitle(dto.getName());

        // Join all locations into a single string
        if (dto.getLocations() != null && !dto.getLocations().isEmpty()) {
            job.setLocation(String.join(", ", dto.getLocations()));
        }

        // Build full application URL
        if (dto.getPositionUrl() != null) {
            job.setUrl("https://careers.microsoft.com" + dto.getPositionUrl());
        }

        // Create searchable description
        StringBuilder description = new StringBuilder();

        if (dto.getDepartment() != null) {
            description.append(dto.getDepartment()).append(". ");
        }

        if (dto.getWorkLocationOption() != null) {
            description.append("Work Location: ")
                    .append(dto.getWorkLocationOption())
                    .append(". ");
        }

        job.setDescription(description.toString().trim());

        // Convert Unix timestamp to LocalDate
        if (dto.getPostedTs() != null) {
            LocalDate postedDate = Instant.ofEpochSecond(dto.getPostedTs())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            job.setPostedDate(postedDate);
        }

        // Experience not explicitly provided
        job.setMinExperience(null);
        job.setMaxExperience(null);

        return job;
    }

    // ======================================================
    // DTO CLASSES
    // ======================================================

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MicrosoftSearchResponse {
        private Integer status;
        private DataDto data;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataDto {
        private List<PositionDto> positions;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PositionDto {
        private Long id;
        private String displayJobId;
        private String name;
        private List<String> locations;
        private Long postedTs;
        private String department;
        private String workLocationOption;
        private String positionUrl;
    }
}