package com.example.spacelab.service;

import com.example.spacelab.dto.student.StudentTaskLessonDTO;
import com.example.spacelab.dto.task.StudentTaskPointDTO;
import com.example.spacelab.model.course.Course;
import com.example.spacelab.model.student.Student;
import com.example.spacelab.model.student.StudentTask;
import com.example.spacelab.util.FilterForm;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Hidden
public interface StudentTaskService {

//    List<StudentTask> getStudentTasks(Long id);
//    List<StudentTask> getStudentTasks(Long id, StudentTaskStatus status);
//    Page<StudentTask> getStudentTasks(Long id, StudentTaskStatus status, Pageable pageable);
//    Page<StudentTask> getStudentTasks(Specification<StudentTask> spec, Pageable pageable);
    Page<StudentTask> getStudentTasks(FilterForm filters, Pageable pageable);
    StudentTask getStudentTask(Long taskID);

    List<StudentTaskLessonDTO> getOpenStudentTasks(Student student);
    List<StudentTaskLessonDTO> getNextStudentTasks(Student student);

    List<StudentTaskPointDTO> getStudentTaskProgressPoints(Long taskId);

    void createStudentTasksOnCourseTransfer(Student student, Course course);

    void completeStudentTask(Long taskID);
    void markStudentTaskAsReady(Long taskID);
    void markStudentTaskAsNotReady(Long taskID);

}
