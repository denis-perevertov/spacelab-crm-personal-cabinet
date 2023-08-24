package com.example.spacelab.service;

import com.example.spacelab.model.student.StudentTask;
import com.example.spacelab.model.student.StudentTaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudentTaskService {

    List<StudentTask> getStudentTasks(Long id);
    List<StudentTask> getStudentTasks(Long id, StudentTaskStatus status);
    Page<StudentTask> getStudentTasks(Long id, StudentTaskStatus status, Pageable pageable);
    StudentTask getStudentTask(Long taskID);


}
