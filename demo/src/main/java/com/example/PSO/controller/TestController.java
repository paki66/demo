package com.example.PSO.controller;

import com.example.PSO.service.TestService;
import nl.martijndwars.webpush.Subscription;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {

    private final TestService testService;

    public TestController(TestService testController) {
        this.testService = testController;
    }


    @PostMapping
    public void subscribe(@RequestBody Subscription subscription) {
        testService.subscribe(subscription);
    }

    @PostMapping("/push")
    public String push(@RequestParam String message) {
        testService.sendNotifications(message);
        return message;
    }

    @GetMapping("/public-key")
    public String getPublicKey() {
        return testService.getPublicKey();
    }

    @DeleteMapping("/")
    public void delete(@RequestParam String endpoint) {
        testService.unsubscribe(endpoint);
    }

    @DeleteMapping("/delete-all")
    public void deleteAll() {
        testService.unsubscribeAll();
    }
}
