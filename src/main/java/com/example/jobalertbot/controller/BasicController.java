package com.example.jobalertbot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class BasicController {

    @GetMapping
    public ResponseEntity<String> getHealthStatus(RequestEntity<?> request) {
        log.info("getHealthStatus called");
        return ResponseEntity.status(200).body("Hi There");
    }
}
