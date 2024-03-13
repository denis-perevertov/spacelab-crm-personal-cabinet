package com.example.spacelab.service;

import com.example.spacelab.dto.student.StudentAvatarEditRequest;
import com.example.spacelab.dto.student.StudentDetailsDTO;
import com.example.spacelab.dto.student.StudentNameDTO;
import com.example.spacelab.dto.student.StudentRegisterRequest;
import com.example.spacelab.integration.data.TimeTotalResponse;
import com.example.spacelab.model.lesson.Lesson;
import com.example.spacelab.model.lesson.LessonReportRow;
import com.example.spacelab.model.student.Student;
import com.example.spacelab.model.student.StudentTask;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Hidden
public interface StudentService extends StudentCardService,
                                        UserDetailsService {

    Student getStudentById(Long id);
    Student registerStudent(Student student);
    Student registerStudent(StudentRegisterRequest request) throws IOException;

    List<LessonReportRow> getStudentLessonData(Long studentID);
    Long getStudentCourseID(Long studentID);

    TimeTotalResponse getStudentTotalLearningTime(Long studentId);
    TimeTotalResponse getStudentRecentLearningTime(Long studentId);

    Optional<StudentTask> getStudentLastCompletedTask(Long studentId);
    long getStudentCompletedTaskAmount(Long studentId);

    Optional<Lesson> getStudentLastVisitedLesson(Long studentId);
    Optional<Lesson> getStudentNextLesson(Long studentId);
    long getStudentVisitedLessonAmount(Long studentId);
    long getStudentSkippedLessonAmount(Long studentId);

    void saveProfileDetails(Long studentId, StudentDetailsDTO dto);
    void saveStudentName(Long studentId, StudentNameDTO dto);
    String saveStudentAvatar(Long studentId, StudentAvatarEditRequest request) throws IOException;
}
