package com.example.jobalertbot.controller;

import com.example.jobalertbot.dto.SignupRequest;
import com.example.jobalertbot.service.SubscriberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subscribers")
public class SubscriberController {

    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest request) {
        subscriberService.subscribe(request.getEmail());
        return ResponseEntity.status(200).body("Subscribed successfully.");
    }

    @PostMapping("/unsubscribe")
    public ResponseEntity<String> unsubscribe(@RequestBody SignupRequest request) {
        subscriberService.unsubscribe(request.getEmail());
        return ResponseEntity.status(200).body("Unsubscribed successfully.");
    }
}