package com.example.spacelab.service;

import com.example.spacelab.dto.student.StudentLessonDisplayDTO;
import com.example.spacelab.model.lesson.Lesson;
import com.example.spacelab.model.task.Task;
import com.example.spacelab.util.FilterForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LessonService extends EntityFilterService<Lesson>{
    List<Lesson> getLessons();
    List<Lesson> getStudentLessons();
    Lesson getLessonById(Long id);
    void startLesson(Long id);

    List<StudentLessonDisplayDTO> getStudentLessonDisplayData(Long id);
}
