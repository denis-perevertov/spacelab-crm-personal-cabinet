package com.example.spacelab.controller;

import com.example.spacelab.api.CourseAPI;
import com.example.spacelab.mapper.CourseMapper;
import com.example.spacelab.service.CourseService;
import com.example.spacelab.util.AuthUtil;
import com.example.spacelab.validator.CourseUpdateValidator;
import com.example.spacelab.validator.CourseValidator;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/course")
public class CourseController implements CourseAPI {

    private final CourseService courseService;
    private final CourseMapper mapper;
    private final CourseValidator courseValidator;
    private final CourseUpdateValidator courseUpdateValidator;

    @GetMapping
    public ResponseEntity<?> getStudentCourseInfo() {
        return ResponseEntity.ok(
                mapper.fromCourseToInfoPageDTO(
                        courseService.getCourseById(AuthUtil.getLoggedInPrincipal().getCourse().getId())
                )
        );
    }

    @GetMapping("/tasks")
    public ResponseEntity<?> getStudentCourseTasks() {
        return ResponseEntity.ok(courseService.getStudentCourseInfo(AuthUtil.getLoggedInPrincipal().getId()));
    }

    @GetMapping("/filter")
    public ResponseEntity<?> loadCourseFilter() {
        return ResponseEntity.ok(courseService.getCourses().stream().map(mapper::fromCourseToSelectDTO).toList());
    }

}
