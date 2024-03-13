package com.example.spacelab.service;

import com.example.spacelab.model.course.Course;
import com.example.spacelab.model.student.StudentTask;
import com.example.spacelab.model.task.Task;
import com.example.spacelab.util.FilterForm;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@Hidden
public interface TaskService extends StudentTaskService,
                                    EntityFilterService<Task>{

    List<Task> getTasks();
    Page<Task> getTasks(FilterForm filters, Pageable pageable);

    Task getTaskById(Long id);

    List<StudentTask> createStudentTaskListFromCourse(Course course);

    File generatePDF(Long taskId, String localeCode) throws IOException, URISyntaxException;
}
