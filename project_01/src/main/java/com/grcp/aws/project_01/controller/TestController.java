package com.grcp.aws.project_01.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);

    @GetMapping("/api/v1/my-test/{string}")
    public ResponseEntity<Void> get(@PathVariable("string") String myString) {
        LOGGER.info("My new value {}", myString);
        return ResponseEntity.noContent().build();
    }
}
