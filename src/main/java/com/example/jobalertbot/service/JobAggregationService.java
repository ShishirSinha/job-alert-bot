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

        // All newly discovered jobs (relevant + non-relevant)
        List<JobPosting> jobsToSave = new ArrayList<>();

        // Subset of jobs that matched the filtering criteria
        List<JobPosting> newlyMatchedJobs = new ArrayList<>();

        for (CompanyCrawler crawler : crawlers) {
            List<JobPosting> jobs = crawler.fetchJobs(criteria);

            for (JobPosting job : jobs) {
                Optional<JobPosting> existing =
                        repository.findByCompanyAndExternalJobId(
                                job.getCompany(),
                                job.getExternalJobId()
                        );

                // Skip if already present in the database
                if (existing.isPresent()) {
                    continue;
                }

                // Calculate relevance score and determine if job matches
                boolean relevant =
                        filterService.isRelevant(job, criteria, minimumScore);

                // Save every new job (relevant or not)
                jobsToSave.add(job);

                // Keep track of relevant jobs for notification
                if (relevant) {
                    newlyMatchedJobs.add(job);
                }
            }
        }

        // Bulk insert all newly discovered jobs
        if (!jobsToSave.isEmpty()) {
            repository.saveAll(jobsToSave);
        }

        log.info("Relevant jobs found: " + newlyMatchedJobs.size());

        // Notify for relevant jobs
        if (!newlyMatchedJobs.isEmpty()) {
            emailService.sendJobAlert(newlyMatchedJobs);

            // Mark as notified
            newlyMatchedJobs.forEach(job -> job.setNotified(true));

            // Bulk update notified flag
            repository.saveAll(newlyMatchedJobs);
        }
    }
}