package com.example.jobalertbot.controller;

import com.example.jobalertbot.service.JobAggregationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jobs")
public class JobController {

    private final JobAggregationService jobAggregationService;

    public JobController(JobAggregationService jobAggregationService) {
        this.jobAggregationService = jobAggregationService;
    }

    @PostMapping("/run")
    public ResponseEntity<String> runJobSearch() {
        jobAggregationService.runJobSearch();
        return ResponseEntity.ok("Job search completed");
    }
}