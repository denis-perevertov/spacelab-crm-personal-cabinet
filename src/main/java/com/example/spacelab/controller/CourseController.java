package com.example.spacelab.controller;

import com.example.spacelab.dto.course.*;
import com.example.spacelab.dto.task.TaskCourseDTO;
import com.example.spacelab.exception.ErrorMessage;
import com.example.spacelab.exception.ObjectValidationException;
import com.example.spacelab.mapper.CourseMapper;
import com.example.spacelab.model.admin.Admin;
import com.example.spacelab.model.course.Course;
import com.example.spacelab.model.course.CourseStatus;
import com.example.spacelab.model.role.PermissionType;
import com.example.spacelab.service.CourseService;
import com.example.spacelab.util.AuthUtil;
import com.example.spacelab.util.FilterForm;
import com.example.spacelab.validator.CourseValidator;
import com.example.spacelab.validator.CourseUpdateValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Tag(name = "Course", description = "Course controller")
@RestController
@Log
@Data
@RequiredArgsConstructor
@RequestMapping("/api/course")
public class CourseController {

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
