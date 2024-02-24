package com.example.spacelab.controller;

import com.example.spacelab.integration.TaskTrackingService;
import com.example.spacelab.integration.data.TaskRequest;
import com.example.spacelab.mapper.TaskMapper;
import com.example.spacelab.model.student.StudentTask;
import com.example.spacelab.service.TaskService;
import com.example.spacelab.util.AuthUtil;
import com.example.spacelab.util.FilterForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    private final TaskTrackingService trackingService;

    @GetMapping
    public ResponseEntity<?> getTasks(FilterForm filters,
                                      @RequestParam(required = false, defaultValue = "0") int page,
                                      @RequestParam(required = false, defaultValue = "5") int size) {

        filters.setStudent(AuthUtil.getLoggedInPrincipal().getId());
        Pageable pageable = PageRequest.of(page, size);
        Page<StudentTask> taskPage = taskService.getStudentTasks(filters.trim(), pageable);
        return ResponseEntity.ok(taskPage.map(taskMapper::fromStudentTaskToDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskInfo(@PathVariable Long id) {
        StudentTask st = taskService.getStudentTask(id);
        return ResponseEntity.ok(taskMapper.studentTaskToCardDTO(st));
    }

    @PutMapping("/{id}/ready")
    public ResponseEntity<?> markTaskAsReady(@PathVariable Long id) {
        taskService.markStudentTaskAsReady(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/not-ready")
    public ResponseEntity<?> markTaskAsNotReady(@PathVariable Long id) {
        taskService.markStudentTaskAsNotReady(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/points")
    public ResponseEntity<?> getTaskProgressPoints(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getStudentTaskProgressPoints(id));
    }

    // Экспорт в PDF
    @GetMapping("/{id}/export/pdf")
    public ResponseEntity<?> exportTaskToPDF(@PathVariable Long id,
                                             @RequestParam(required = false, defaultValue = "ua") String locale) throws IOException {
        File file;
        try {
            file = taskService.generatePDF(id, locale);
        } catch (Exception e) {
            log.error(e.getClass().toString());
            log.error(e.getMessage());
            return ResponseEntity.unprocessableEntity().body("Could not generate pdf file");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData(file.getName(), file.getName());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(
                new InputStreamResource(Files.newInputStream(file.getAbsoluteFile().toPath(), StandardOpenOption.DELETE_ON_CLOSE)),
                headers,
                HttpStatus.OK
        );
    }

    @PutMapping("/points/complete")
    public ResponseEntity<?> completeProgressPoint(@RequestBody String pointId) {
        trackingService.completeTask(pointId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/points/uncomplete")
    public ResponseEntity<?> uncompleteProgressPoint(@RequestBody String pointId) {
        trackingService.uncompleteTask(pointId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/points/create")
    public ResponseEntity<?> createProgressPoint(@RequestBody TaskRequest request) {
        return ResponseEntity.ok(trackingService.createTaskInTaskList(request));
    }

    @PutMapping("/points/edit")
    public ResponseEntity<?> editProgressPoint(@RequestBody TaskRequest request) {
        return ResponseEntity.ok(trackingService.updateTask(request));
    }

    @DeleteMapping("/points/delete")
    public ResponseEntity<?> deleteProgressPoint(@RequestBody String pointId) {
        trackingService.deleteTask(pointId);
        return ResponseEntity.ok().build();
    }
}
