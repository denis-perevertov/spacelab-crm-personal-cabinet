package com.example.spacelab.controller;

import com.example.spacelab.api.LessonAPI;
import com.example.spacelab.dto.lesson.LessonInfoDTO;
import com.example.spacelab.mapper.LessonMapper;
import com.example.spacelab.service.LessonService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lessons")
public class LessonController implements LessonAPI {

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
