package com.example.jobalertbot.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class JobSearchCriteria {

    private List<String> titles;
    private List<String> locations;
    private int minExperience;
    private int maxExperience;
    private List<String> requiredSkills;
    private List<String> excludedKeywords;

    // Getters and setters
}