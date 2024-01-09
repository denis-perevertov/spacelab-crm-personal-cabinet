package com.example.spacelab.service;

import com.example.spacelab.dto.course.CourseEditDTO;
import com.example.spacelab.dto.course.CourseIconDTO;
import com.example.spacelab.dto.course.StudentCourseTaskInfoDTO;
import com.example.spacelab.dto.task.TaskCourseDTO;
import com.example.spacelab.model.course.Course;
import com.example.spacelab.model.student.Student;
import com.example.spacelab.model.task.Task;
import com.example.spacelab.util.FilterForm;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface CourseService extends EntityFilterService<Course> {

    List<Course> getCourses();
    Course getCourseById(Long id);
    List<Task> getCourseTasks(Long id);
    StudentCourseTaskInfoDTO getStudentCourseInfo(Long studentID);
}
