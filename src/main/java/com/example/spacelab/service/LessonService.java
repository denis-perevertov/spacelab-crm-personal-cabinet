package com.example.spacelab.service;

import com.example.spacelab.dto.student.StudentLessonDisplayDTO;
import com.example.spacelab.model.lesson.Lesson;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.List;

@Hidden
public interface LessonService extends EntityFilterService<Lesson>{
    List<Lesson> getLessons();
    List<Lesson> getStudentLessons();
    Lesson getLessonById(Long id);
    void startLesson(Long id);

    List<StudentLessonDisplayDTO> getStudentLessonDisplayData(Long id);
}
