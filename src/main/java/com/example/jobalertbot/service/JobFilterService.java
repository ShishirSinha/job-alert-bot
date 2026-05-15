package com.example.jobalertbot.service;

import com.example.jobalertbot.model.JobPosting;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class JobFilterService {

    public int calculateRelevanceScore(JobPosting job,
                                       JobSearchCriteria criteria) {
        int score = 0;

        if (titleMatches(job.getTitle(), criteria.getTitles())) {
            score += 30;
        }

        if (locationMatches(job.getLocation(), criteria.getLocations())) {
            score += 20;
        }

        if (experienceMatches(job, criteria)) {
            score += 20;
        }

        score += calculateSkillScore(
                job.getDescription(),
                criteria.getRequiredSkills()
        );

        return score;
    }

    public boolean isRelevant(JobPosting job,
                              JobSearchCriteria criteria,
                              int minimumScore) {
        if (containsExcludedKeywords(
                job.getTitle(),
                criteria.getExcludedKeywords())) {
            return false;
        }

        int score = calculateRelevanceScore(job, criteria);
        job.setRelevanceScore(score);

        return score >= minimumScore;
    }

    private boolean titleMatches(String title, List<String> keywords) {
        if (title == null || keywords == null) {
            return false;
        }

        String normalized = title.toLowerCase(Locale.ROOT);

        return keywords.stream()
                .map(k -> k.toLowerCase(Locale.ROOT))
                .anyMatch(normalized::contains);
    }

    private boolean locationMatches(String location,
                                    List<String> preferredLocations) {
        if (location == null || preferredLocations == null) {
            return false;
        }

        String normalized = location.toLowerCase(Locale.ROOT);

        return preferredLocations.stream()
                .map(loc -> loc.toLowerCase(Locale.ROOT))
                .anyMatch(normalized::contains);
    }

    private boolean experienceMatches(JobPosting job,
                                      JobSearchCriteria criteria) {
        if (job.getMinExperience() == null &&
                job.getMaxExperience() == null) {
            // If experience is not provided by the company,
            // don't penalize the job.
            return true;
        }

        Integer min = job.getMinExperience();
        Integer max = job.getMaxExperience();

        int preferredMin = criteria.getMinExperience();
        int preferredMax = criteria.getMaxExperience();

        if (min != null && min > preferredMax) {
            return false;
        }

        if (max != null && max < preferredMin) {
            return false;
        }

        return true;
    }

    private int calculateSkillScore(String description,
                                    List<String> skills) {
        if (description == null || skills == null || skills.isEmpty()) {
            return 0;
        }

        String normalized = description.toLowerCase(Locale.ROOT);

        long matched = skills.stream()
                .map(skill -> skill.toLowerCase(Locale.ROOT))
                .filter(normalized::contains)
                .count();

        return (int) ((matched * 30.0) / skills.size());
    }

    private boolean containsExcludedKeywords(String text,
                                             List<String> excludedKeywords) {
        if (text == null || excludedKeywords == null) {
            return false;
        }

        String normalized = text.toLowerCase(Locale.ROOT);

        return excludedKeywords.stream()
                .map(k -> k.toLowerCase(Locale.ROOT))
                .anyMatch(normalized::contains);
    }
}