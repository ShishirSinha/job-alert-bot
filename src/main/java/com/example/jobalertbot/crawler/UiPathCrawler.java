package com.example.jobalertbot.crawler;

import com.example.jobalertbot.model.JobPosting;
import com.example.jobalertbot.service.JobSearchCriteria;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class UiPathCrawler implements CompanyCrawler {

    private static final String JOBS_API_URL = "https://uipath.com/api/getjobs";

    private final WebClient webClient;

    public UiPathCrawler(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public String getCompanyName() {
        return "UiPath";
    }

    @Override
    public List<JobPosting> fetchJobs(JobSearchCriteria criteria) {
        try {
            UiPathJobDto[] jobs = webClient.get()
                    .uri(JOBS_API_URL)
                    .header("Accept", "application/json")
                    .retrieve()
                    .bodyToMono(UiPathJobDto[].class)
                    .block();

            if (jobs == null || jobs.length == 0) {
                return Collections.emptyList();
            }

            return Arrays.stream(jobs)
                    .filter(job -> Boolean.TRUE.equals(job.getIsListed()))
                    .map(this::toJobPosting)
                    .toList();

        } catch (Exception e) {
            System.err.println("Failed to fetch jobs from UiPath: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private JobPosting toJobPosting(UiPathJobDto dto) {
        JobPosting posting = new JobPosting();

        posting.setCompany(getCompanyName());

        // Use id as the unique external identifier
        posting.setExternalJobId(dto.getId());

        posting.setTitle(dto.getTitle());

        // Prefer locationName, fallback to city + country
        if (dto.getLocationName() != null && !dto.getLocationName().isBlank()) {
            posting.setLocation(dto.getLocationName());
        } else {
            String city = dto.getLocationCity() != null ? dto.getLocationCity() : "";
            String country = dto.getLocationCountry() != null ? dto.getLocationCountry() : "";
            posting.setLocation((city + ", " + country).trim());
        }

        // Use applyLink if available, otherwise externalLink
        if (dto.getApplyLink() != null && !dto.getApplyLink().isBlank()) {
            posting.setUrl(dto.getApplyLink());
        } else {
            posting.setUrl(dto.getExternalLink());
        }

        // Build a searchable description using available fields
        StringBuilder description = new StringBuilder();

        if (dto.getEmploymentType() != null) {
            description.append(dto.getEmploymentType()).append(". ");
        }

        if (dto.getJobLevel() != null) {
            description.append("Level: ").append(dto.getJobLevel()).append(". ");
        }

        posting.setDescription(description.toString().trim());

        LocalDate publishedDate = dto.getPublishedDate();
        posting.setPostedDate(publishedDate);

        // Experience is not provided by the API
        posting.setMinExperience(null);
        posting.setMaxExperience(null);

        return posting;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UiPathJobDto {

        private String id;
        private String title;
        private String jobId;
        private String locationName;
        private String locationCity;
        private String locationCountry;
        private String externalLink;
        private String applyLink;
        private String employmentType;
        private Boolean isListed;
        private LocalDate publishedDate;
        private String jobLevel;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getJobId() {
            return jobId;
        }

        public void setJobId(String jobId) {
            this.jobId = jobId;
        }

        public String getLocationName() {
            return locationName;
        }

        public void setLocationName(String locationName) {
            this.locationName = locationName;
        }

        public String getLocationCity() {
            return locationCity;
        }

        public void setLocationCity(String locationCity) {
            this.locationCity = locationCity;
        }

        public String getLocationCountry() {
            return locationCountry;
        }

        public void setLocationCountry(String locationCountry) {
            this.locationCountry = locationCountry;
        }

        public String getExternalLink() {
            return externalLink;
        }

        public void setExternalLink(String externalLink) {
            this.externalLink = externalLink;
        }

        public String getApplyLink() {
            return applyLink;
        }

        public void setApplyLink(String applyLink) {
            this.applyLink = applyLink;
        }

        public String getEmploymentType() {
            return employmentType;
        }

        public void setEmploymentType(String employmentType) {
            this.employmentType = employmentType;
        }

        public Boolean getIsListed() {
            return isListed;
        }

        public void setIsListed(Boolean listed) {
            isListed = listed;
        }

        public LocalDate getPublishedDate() {
            return publishedDate;
        }

        public void setPublishedDate(LocalDate publishedDate) {
            this.publishedDate = publishedDate;
        }

        public String getJobLevel() {
            return jobLevel;
        }

        public void setJobLevel(String jobLevel) {
            this.jobLevel = jobLevel;
        }
    }
}