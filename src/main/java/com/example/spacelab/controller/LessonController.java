package com.example.spacelab.controller;

import com.example.spacelab.dto.lesson.LessonInfoDTO;
import com.example.spacelab.dto.lesson.LessonListDTO;
import com.example.spacelab.dto.lesson.LessonReportRowSaveDTO;
import com.example.spacelab.dto.lesson.LessonSaveBeforeStartDTO;
import com.example.spacelab.exception.ErrorMessage;
import com.example.spacelab.exception.ObjectValidationException;
import com.example.spacelab.job.LessonMonitor;
import com.example.spacelab.mapper.LessonMapper;
import com.example.spacelab.model.admin.Admin;
import com.example.spacelab.model.course.Course;
import com.example.spacelab.model.lesson.Lesson;
import com.example.spacelab.model.lesson.LessonStatus;
import com.example.spacelab.model.literature.LiteratureType;
import com.example.spacelab.model.role.PermissionType;
import com.example.spacelab.service.LessonReportRowService;
import com.example.spacelab.service.LessonService;
import com.example.spacelab.util.AuthUtil;
import com.example.spacelab.util.FilterForm;
import com.example.spacelab.validator.LessonBeforeStartValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.apache.coyote.Response;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name="Lesson", description = "Lesson controller")
@RestController
@Log
@RequiredArgsConstructor
@RequestMapping("/api/lessons")
public class LessonController {

    private final LessonService lessonService;
    private final LessonMapper lessonMapper;

    @GetMapping
    public ResponseEntity<?> getStudentLessons() {
        return ResponseEntity.ok(
                lessonService.getStudentLessons()
                        .stream()
                        .map(lessonMapper::fromLessonToLessonListDTO)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<LessonInfoDTO> getLessonById(@PathVariable @Parameter(example = "1") Long id) {
        LessonInfoDTO less = lessonMapper.fromLessonToLessonInfoDTO(lessonService.getLessonById(id));
        return new ResponseEntity<>(less, HttpStatus.OK);
    }

    // получение всех страничек данных у студентов курса занятия
    @GetMapping("/{id}/student-lesson-display-data")
    public ResponseEntity<?> getStudentLessonDisplayData(@PathVariable Long id) {
        return ResponseEntity.ok(lessonService.getStudentLessonDisplayData(id));
    }

}
