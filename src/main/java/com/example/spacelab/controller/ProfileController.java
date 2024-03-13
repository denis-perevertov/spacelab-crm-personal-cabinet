package com.example.spacelab.controller;

import com.example.spacelab.dto.SelectDTO;
import com.example.spacelab.dto.student.StudentAvatarEditRequest;
import com.example.spacelab.dto.student.StudentDetailsDTO;
import com.example.spacelab.dto.student.StudentNameDTO;
import com.example.spacelab.integration.TaskTrackingService;
import com.example.spacelab.mapper.LessonMapper;
import com.example.spacelab.mapper.StudentMapper;
import com.example.spacelab.mapper.TaskMapper;
import com.example.spacelab.model.student.StudentEducationLevel;
import com.example.spacelab.model.student.StudentEnglishLevel;
import com.example.spacelab.model.student.StudentWorkStatus;
import com.example.spacelab.service.*;
import com.example.spacelab.util.AuthUtil;
import com.example.spacelab.validator.StudentValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController("myProfileController")
@Transactional
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {

    private final CourseService courseService;
    private final StudentService studentService;
    private final TaskService taskService;
    private final LessonService lessonService;
    private final AdminService adminService;
    private final TaskTrackingService trackingService;
    private final StudentMapper studentMapper;
    private final TaskMapper taskMapper;
    private final LessonMapper lessonMapper;

    private final StudentValidator validator;

    @GetMapping
    public ResponseEntity<?> getStudentProfileCard() {
        Long student = AuthUtil.getLoggedInPrincipal().getId();
        return ResponseEntity.ok(studentService.getCard(student));
    }

    @PatchMapping("/avatar")
    public ResponseEntity<?> editStudentAvatar(@ModelAttribute StudentAvatarEditRequest request,
                                               BindingResult bindingResult) {
        validator.validateStudentAvatar(request, bindingResult);
        if(bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        Long student = AuthUtil.getLoggedInPrincipal().getId();
        try {
            String newFileName = studentService.saveStudentAvatar(student, request);
            return ResponseEntity.ok(newFileName);
        } catch (IOException e) {
            return ResponseEntity.unprocessableEntity().build();
        }
    }

    @PatchMapping("/name")
    public ResponseEntity<?> editStudentName(@RequestBody @Valid StudentNameDTO dto,
                                            BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        Long student = AuthUtil.getLoggedInPrincipal().getId();
        studentService.saveStudentName(student, dto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/details")
    public ResponseEntity<?> editStudentDetails(@RequestBody @Valid StudentDetailsDTO details,
                                                BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        Long student = AuthUtil.getLoggedInPrincipal().getId();
        studentService.saveProfileDetails(student, details);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/learning-time")
    public ResponseEntity<?> getStudentTotalLearningTime() {
        Long student = AuthUtil.getLoggedInPrincipal().getId();
        return ResponseEntity.ok(Map.of(
                "total", studentService.getStudentTotalLearningTime(student),
                "recent", studentService.getStudentRecentLearningTime(student)
        ));
    }

//    @GetMapping("/student-profile-graphs")
//    public ResponseEntity<?> getStudentLearningTimeDistribution(,
//                                                                @RequestParam(required = false) String fromString,
//                                                                @RequestParam(required = false) String toString) {
//        LocalDateTime from = fromString != null && !fromString.isEmpty()
//                ? LocalDate.parse(fromString, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay()
//                : null;
//        LocalDateTime to = toString != null && !toString.isEmpty()
//                ? LocalDate.parse(toString, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atTime(LocalTime.MAX)
//                : null;
//        log.info("from: {}, to: {}", from, to);
//        return ResponseEntity.ok(Map.of(
//                "learningTime", studentService.getStudentLearningTimeDistribution(student, from, to),
//                "rating", studentService.getStudentRatingDistribution(student, from, to)
//        ));
//    }

    @GetMapping("/task-cards")
    public ResponseEntity<?> getStudentTaskCards() {
        Long student = AuthUtil.getLoggedInPrincipal().getId();
        return ResponseEntity.ok(Map.of(
                "lastCompleted", studentService.getStudentLastCompletedTask(student).map(taskMapper::studentTaskToLinkDTO),
                "completedAmount", studentService.getStudentCompletedTaskAmount(student)
        ));
    }

    @GetMapping("/lesson-cards")
    public ResponseEntity<?> getStudentLessonCards() {
        Long student = AuthUtil.getLoggedInPrincipal().getId();
        return ResponseEntity.ok(Map.of(
                "lastVisitedLesson", studentService.getStudentLastVisitedLesson(student).map(lessonMapper::fromLessonToLinkDTO),
                "nextLesson", studentService.getStudentNextLesson(student).map(lessonMapper::fromLessonToLinkDTO),
                "visitedAmount", studentService.getStudentVisitedLessonAmount(student),
                "skippedAmount", studentService.getStudentSkippedLessonAmount(student)
        ));
    }

    @GetMapping("/education-levels")
    public ResponseEntity<?> getEducationLevelList() {
        return ResponseEntity.ok(Arrays.stream(StudentEducationLevel.values()).map(v -> new SelectDTO(v.name(), v.name())).toList());
    }
    @GetMapping("/english-levels")
    public ResponseEntity<?> getEnglishLevelList() {
        return ResponseEntity.ok(Arrays.stream(StudentEnglishLevel.values()).map(v -> new SelectDTO(v.name(), v.name())).toList());
    }
    @GetMapping("/work-statuses")
    public ResponseEntity<?> getWorkStatusList() {
        return ResponseEntity.ok(Arrays.stream(StudentWorkStatus.values()).map(v -> new SelectDTO(v.name(), v.name())).toList());
    }


//    @GetMapping("/student-rating")
//    public ResponseEntity<?> getStudentAverageAndRecentRating() {
//        return ResponseEntity.ok(Map.of(
//                "total", studentService.getStudentTotalAverageRating(student),
//                "recent", studentService.getStudentRecentAverageRating(student)
//        ));
//    }
}
