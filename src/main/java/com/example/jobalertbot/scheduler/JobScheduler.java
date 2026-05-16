package com.example.jobalertbot.scheduler;

import com.example.jobalertbot.service.JobAggregationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JobScheduler {

    private static final Logger log =
            LoggerFactory.getLogger(JobScheduler.class);

    private final JobAggregationService jobAggregationService;

    public JobScheduler(JobAggregationService jobAggregationService) {
        this.jobAggregationService = jobAggregationService;
    }

    @Scheduled(cron = "${app.scheduler.cron}", zone = "Asia/Kolkata")
    public void runScheduledJobSearch() {
        log.info("Starting scheduled job search");

        try {
            jobAggregationService.runJobSearch();
            log.info("Scheduled job search completed successfully");
        } catch (Exception e) {
            log.error("Scheduled job search failed", e);
        }
    }
}