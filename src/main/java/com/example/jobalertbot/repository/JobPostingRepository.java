package com.example.jobalertbot.repository;

import com.example.jobalertbot.model.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    Optional<JobPosting> findByCompanyAndExternalJobId(
            String company,
            String externalJobId
    );

    List<JobPosting> findByNotifiedFalseAndRelevanceScoreGreaterThanEqual(
            Integer minimumScore
    );
}