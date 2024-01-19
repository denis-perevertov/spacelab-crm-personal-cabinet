package com.example.spacelab.controller;

import com.example.spacelab.dto.SelectSearchDTO;
import com.example.spacelab.dto.student.StudentTaskDTO;
import com.example.spacelab.dto.student.*;
import com.example.spacelab.dto.task.TaskCourseDTO;
import com.example.spacelab.exception.ErrorMessage;
import com.example.spacelab.exception.ObjectValidationException;
import com.example.spacelab.mapper.CourseMapper;
import com.example.spacelab.mapper.StudentMapper;
import com.example.spacelab.mapper.TaskMapper;
import com.example.spacelab.model.admin.Admin;
import com.example.spacelab.model.course.Course;
import com.example.spacelab.model.lesson.LessonReportRow;
import com.example.spacelab.model.role.PermissionType;
import com.example.spacelab.model.student.Student;
import com.example.spacelab.model.student.StudentAccountStatus;
import com.example.spacelab.model.student.StudentInviteRequest;
import com.example.spacelab.model.student.StudentTaskStatus;
import com.example.spacelab.service.CourseService;
import com.example.spacelab.service.StudentService;
import com.example.spacelab.util.AuthUtil;
import com.example.spacelab.util.FilterForm;
import com.example.spacelab.validator.StudentValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
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

import java.util.*;


@RestController
@Log
@RequiredArgsConstructor
@RequestMapping("/api/students")
public class StudentController {

    private final CourseService courseService;
    private final StudentService studentService;
    private final StudentMapper studentMapper;
    private final StudentValidator studentValidator;
    private final TaskMapper taskMapper;
    private final CourseMapper courseMapper;

    private final AuthUtil authUtil;

    // Получение одного студента
    @GetMapping("/{studentID}")
    public ResponseEntity<StudentDTO> getStudent(@PathVariable @Parameter(example = "1") Long studentID) {

        Student student = studentService.getStudentById(studentID);
        return new ResponseEntity<>(studentMapper.fromStudentToDTO(student), HttpStatus.OK);
    }

    // Получение карточки информации о студенте
    @GetMapping("/{studentID}/card")
    public ResponseEntity<StudentCardDTO> getStudentCard(@PathVariable @Parameter(example = "1") Long studentID) {

        StudentCardDTO card = studentService.getCard(studentID);
        return new ResponseEntity<>(card, HttpStatus.OK);
    }

    // Получение списка занятий студента
    @GetMapping("/{studentID}/lessons")
    public ResponseEntity<List<LessonReportRow>> getStudentLessons(@PathVariable @Parameter(example = "1") Long studentID) {
        List<LessonReportRow> studentLessonData = studentService.getStudentLessonData(studentID);
        return new ResponseEntity<>(studentLessonData, HttpStatus.OK);
    }

    // получить задания текущего курса студента
    @GetMapping("/{studentID}/course/tasks")
    public ResponseEntity<?> getStudentCourseTasks(@PathVariable Long studentID) {
        List<TaskCourseDTO> courseTaskList =
                courseService.getCourseTasks(studentService.getStudentCourseID(studentID))
                        .stream()
                        .map(courseMapper::fromTaskToCourseDTO)
                        .toList();
        return ResponseEntity.ok(courseTaskList);
    }

//    // Регистрация студента ; для захода сюда защита не нужна
//    @PostMapping("/register")
//    public ResponseEntity<StudentDTO> registerStudent(@RequestBody StudentRegisterDTO dto,
//                                                      BindingResult bindingResult) {
//
//        studentValidator.validate(dto, bindingResult);
//
//        if(bindingResult.hasErrors()) {
//            Map<String, String> errors = new HashMap<>();
//            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
//            throw new ObjectValidationException(errors);
//        }
//
//        Student student = studentService.registerStudent(studentMapper.fromRegisterDTOToStudent(dto));
//        return new ResponseEntity<>(studentMapper.fromStudentToDTO(student), HttpStatus.CREATED);
//    }

    // Удаление студента
    // todo

}
