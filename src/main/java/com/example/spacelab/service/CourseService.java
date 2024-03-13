package com.example.spacelab.service;

import com.example.spacelab.dto.course.StudentCourseTaskInfoDTO;
import com.example.spacelab.model.course.Course;
import com.example.spacelab.model.task.Task;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.List;

@Hidden
public interface CourseService extends EntityFilterService<Course> {

    List<Course> getCourses();
    Course getCourseById(Long id);
    List<Task> getCourseTasks(Long id);
    StudentCourseTaskInfoDTO getStudentCourseInfo(Long studentID);
}
