package com.example.jobalertbot.controller;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BasicController {

    @GetMapping
    public ResponseEntity<String> getHealthStatus(RequestEntity<?> request) {
        System.out.println("getHealthStatus called");
        return ResponseEntity.status(200).body("Hi There");
    }
}
