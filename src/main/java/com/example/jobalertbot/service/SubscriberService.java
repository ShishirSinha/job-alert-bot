package com.example.jobalertbot.service;

import com.example.jobalertbot.model.JobPosting;
import com.example.jobalertbot.model.Subscriber;
import com.example.jobalertbot.repository.JobPostingRepository;
import com.example.jobalertbot.repository.SubscriberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriberService {

    private final SubscriberRepository subscriberRepository;
    private final JobPostingRepository jobPostingRepository;
    private final EmailService emailService;

    @Value("${app.criteria.minimum-score}")
    private int minimumScore;

    public SubscriberService(
            SubscriberRepository subscriberRepository,
            JobPostingRepository jobPostingRepository,
            EmailService emailService
    ) {
        this.subscriberRepository = subscriberRepository;
        this.jobPostingRepository = jobPostingRepository;
        this.emailService = emailService;
    }

    public void subscribe(String email) {
        Subscriber subscriber = subscriberRepository
                .findByEmail(email)
                .orElseGet(() -> {
                    Subscriber s = new Subscriber();
                    s.setEmail(email);
                    return s;
                });

        subscriber.setActive(true);
        subscriberRepository.save(subscriber);

        // Send all currently active relevant jobs immediately
        List<JobPosting> activeJobs = jobPostingRepository.findByActiveTrueAndRelevanceScoreGreaterThanEqual(minimumScore);

        if (!activeJobs.isEmpty()) {
            emailService.sendJobAlert(activeJobs, List.of(email));
        }
    }

    public void unsubscribe(String email) {
        subscriberRepository.findByEmail(email)
                .ifPresent(subscriber -> {
                    subscriber.setActive(false);
                    subscriberRepository.save(subscriber);
                });
    }
}