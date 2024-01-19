package com.example.spacelab.controller;

import com.example.spacelab.integration.TaskTrackingService;
import com.example.spacelab.integration.data.TaskRequest;
import com.example.spacelab.mapper.TaskMapper;
import com.example.spacelab.model.student.StudentTask;
import com.example.spacelab.model.task.Task;
import com.example.spacelab.service.TaskService;
import com.example.spacelab.util.AuthUtil;
import com.example.spacelab.util.FilterForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        Page<StudentTask> taskPage = taskService.getStudentTasks(filters, pageable);
        return ResponseEntity.ok(taskPage.map(taskMapper::fromStudentTaskToDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskInfo(@PathVariable Long id) {
        StudentTask st = taskService.getStudentTask(id);
        return ResponseEntity.ok(taskMapper.studentTaskToCardDTO(st));
    }

    @GetMapping("/{id}/points")
    public ResponseEntity<?> getTaskProgressPoints(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getStudentTaskProgressPoints(id));
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
