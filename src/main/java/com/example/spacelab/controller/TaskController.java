package com.example.spacelab.controller;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    @GetMapping
    public ResponseEntity<?> getTasks(FilterForm filters,
                                      @RequestParam(required = false, defaultValue = "0") int page,
                                      @RequestParam(required = false, defaultValue = "5") int size) {

        filters.setStudent(AuthUtil.getLoggedInPrincipal().getId());
        Pageable pageable = PageRequest.of(page, size);
        Page<StudentTask> taskPage = taskService.getStudentTasks(filters, pageable);
        return ResponseEntity.ok(taskPage.map(taskMapper::fromStudentTaskToDTO));
    }
}
