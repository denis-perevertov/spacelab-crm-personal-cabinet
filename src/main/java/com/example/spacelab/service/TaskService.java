package com.example.spacelab.service;

import com.example.spacelab.model.course.Course;
import com.example.spacelab.model.student.Student;
import com.example.spacelab.model.student.StudentTask;
import com.example.spacelab.model.task.Task;
import com.example.spacelab.util.FilterForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TaskService extends StudentTaskService,
                                    EntityFilterService<Task>{

    List<Task> getTasks();
    Page<Task> getTasks(FilterForm filters, Pageable pageable);

    Task getTaskById(Long id);

    List<StudentTask> createStudentTaskListFromCourse(Course course);

}
