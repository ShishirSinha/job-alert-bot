package com.example.jobalertbot.service;

import com.example.jobalertbot.crawler.CompanyCrawler;
import com.example.jobalertbot.model.JobPosting;
import com.example.jobalertbot.repository.JobPostingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class JobAggregationService {

    private final List<CompanyCrawler> crawlers;
    private final JobPostingRepository repository;
    private final JobFilterService filterService;
    private final JobSearchCriteriaProvider criteriaProvider;
    private final EmailService emailService;

    @Value("${app.criteria.minimum-score}")
    private int minimumScore;

    public JobAggregationService(
            List<CompanyCrawler> crawlers,
            JobPostingRepository repository,
            JobFilterService filterService,
            JobSearchCriteriaProvider criteriaProvider,
            EmailService emailService) {
        this.crawlers = crawlers;
        this.repository = repository;
        this.filterService = filterService;
        this.criteriaProvider = criteriaProvider;
        this.emailService = emailService;
    }

    @Transactional
    public void runJobSearch() {
        JobSearchCriteria criteria = criteriaProvider.getCriteria();

        List<JobPosting> jobsToSave = new ArrayList<>();
        List<JobPosting> newlyMatchedJobs = new ArrayList<>();

        for (CompanyCrawler crawler : crawlers) {
            List<JobPosting> fetchedJobs = crawler.fetchJobs(criteria);

            if (fetchedJobs.isEmpty()) {
                continue;
            }

            // Mark all jobs from this company as inactive
            String company = fetchedJobs.get(0).getCompany();
            List<JobPosting> existingCompanyJobs = repository.findAll().stream()
                    .filter(job -> company.equals(job.getCompany()))
                    .toList();

            existingCompanyJobs.forEach(job -> job.setActive(false));
            jobsToSave.addAll(existingCompanyJobs);

            // Process current jobs
            for (JobPosting job : fetchedJobs) {
                boolean relevant = filterService.isRelevant(job, criteria, minimumScore);

                Optional<JobPosting> existing = repository.findByCompanyAndExternalJobId(
                        job.getCompany(),
                        job.getExternalJobId()
                );

                if (existing.isPresent()) {
                    JobPosting existingJob = existing.get();

                    existingJob.setTitle(job.getTitle());
                    existingJob.setLocation(job.getLocation());
                    existingJob.setUrl(job.getUrl());
                    existingJob.setDescription(job.getDescription());
                    existingJob.setRelevanceScore(job.getRelevanceScore());
                    existingJob.setActive(true);

                    jobsToSave.add(existingJob);
                } else {
                    job.setActive(true);
                    jobsToSave.add(job);

                    if (relevant) {
                        newlyMatchedJobs.add(job);
                    }
                }
            }
        }

        // Bulk save inserts + updates
        if (!jobsToSave.isEmpty()) {
            repository.saveAll(jobsToSave);
        }

        log.info("Relevant jobs found: " + newlyMatchedJobs.size());

        // Notify for relevant jobs
        if (!newlyMatchedJobs.isEmpty()) {
            emailService.sendJobAlert(newlyMatchedJobs);

            newlyMatchedJobs.forEach(job -> job.setNotified(true));
            repository.saveAll(newlyMatchedJobs);
        }
    }

    public void resendActiveJobs() {
        List<JobPosting> activeJobs = repository.findByActiveTrueAndRelevanceScoreGreaterThanEqual(minimumScore);
        log.info("Active jobs found: " + activeJobs.size());

        if (!activeJobs.isEmpty()) {
            emailService.sendJobAlert(activeJobs);
        }
    }
}