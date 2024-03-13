package com.example.spacelab.controller;

import com.example.spacelab.integration.TaskTrackingService;
import com.example.spacelab.integration.data.TimeEntryRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/time")
@RequiredArgsConstructor
public class TimelogController {

    private final TaskTrackingService trackingService;

    @PostMapping("/course")
    public ResponseEntity<?> createTimeEntryForCourse(@RequestBody TimeEntryRequest request) {
        return ResponseEntity.ok(trackingService.createTimeEntryForProject(request));
    }

    @PostMapping("/task")
    public ResponseEntity<?> createTimeEntryForProgressPoint(@RequestBody TimeEntryRequest request) {
        return ResponseEntity.ok(trackingService.createTimeEntryForTask(request));
    }
}
