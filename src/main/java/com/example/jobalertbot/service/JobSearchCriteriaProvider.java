package com.example.jobalertbot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class JobSearchCriteriaProvider {

    @Value("${app.criteria.titles}")
    private String titles;

    @Value("${app.criteria.locations}")
    private String locations;

    @Value("${app.criteria.min-experience}")
    private int minExperience;

    @Value("${app.criteria.max-experience}")
    private int maxExperience;

    @Value("${app.criteria.required-skills}")
    private String requiredSkills;

    @Value("${app.criteria.excluded-keywords}")
    private String excludedKeywords;

    public JobSearchCriteria getCriteria() {
        JobSearchCriteria criteria = new JobSearchCriteria();

        criteria.setTitles(parseList(titles));
        criteria.setLocations(parseList(locations));
        criteria.setMinExperience(minExperience);
        criteria.setMaxExperience(maxExperience);
        criteria.setRequiredSkills(parseList(requiredSkills));
        criteria.setExcludedKeywords(parseList(excludedKeywords));

        return criteria;
    }

    private List<String> parseList(String value) {
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
    }
}