package com.example.spacelab.service;

import com.example.spacelab.dto.student.StudentRegisterDTO;
import com.example.spacelab.dto.student.StudentRegisterRequest;
import com.example.spacelab.model.course.Course;
import com.example.spacelab.model.lesson.LessonReportRow;
import com.example.spacelab.model.student.StudentInviteRequest;
import com.example.spacelab.model.student.Student;
import com.example.spacelab.util.FilterForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.List;

public interface StudentService extends StudentCardService,
                                        UserDetailsService {

    Student getStudentById(Long id);
    Student registerStudent(Student student);
    Student registerStudent(StudentRegisterRequest request) throws IOException;

    List<LessonReportRow> getStudentLessonData(Long studentID);
    Long getStudentCourseID(Long studentID);
}
