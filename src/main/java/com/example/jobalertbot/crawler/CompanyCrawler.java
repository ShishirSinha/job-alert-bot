package com.example.jobalertbot.crawler;

import com.example.jobalertbot.model.JobPosting;
import com.example.jobalertbot.service.JobSearchCriteria;

import java.util.List;

public interface CompanyCrawler {

    String getCompanyName();

    List<JobPosting> fetchJobs(JobSearchCriteria criteria);
}