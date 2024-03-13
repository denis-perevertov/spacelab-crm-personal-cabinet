package com.example.spacelab.service.impl;

import com.example.spacelab.dto.course.CourseEditDTO;
import com.example.spacelab.dto.course.StudentCourseTaskInfoDTO;
import com.example.spacelab.exception.ResourceNotFoundException;
import com.example.spacelab.mapper.CourseMapper;
import com.example.spacelab.mapper.TaskMapper;
import com.example.spacelab.model.admin.Admin;
import com.example.spacelab.model.course.Course;
import com.example.spacelab.model.course.CourseStatus;
import com.example.spacelab.model.student.Student;
import com.example.spacelab.model.task.Task;
import com.example.spacelab.repository.*;
import com.example.spacelab.service.CourseService;
import com.example.spacelab.service.FileService;
import com.example.spacelab.service.StudentService;
import com.example.spacelab.service.StudentTaskService;
import com.example.spacelab.service.specification.CourseSpecifications;
import com.example.spacelab.util.FilterForm;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final StudentTaskRepository studentTaskRepository;
    private final StudentRepository studentRepository;
    private final TaskRepository taskRepository;

    private final StudentService studentService;
    private final StudentTaskService studentTaskService;

    private final AdminRepository adminRepository;
    private final CourseRepository courseRepository;

    private final CourseMapper mapper;
    private final TaskMapper taskMapper;

    private final FileService fileService;

    @Override
    public List<Course> getCourses() {
        return courseRepository.findAll();
    }

    @Override
    public List<Task> getCourseTasks(Long id) {
        if(id == null) return new ArrayList<>();
        else return getCourseById(id).getTasks().stream().toList();
    }

    @Override
    public StudentCourseTaskInfoDTO getStudentCourseInfo(Long studentID) {
        Student student = studentService.getStudentById(studentID);
        Course studentCourse = student.getCourse();
        return new StudentCourseTaskInfoDTO(
                studentCourse.getId(),
                studentCourse.getName(),
                studentCourse.getIcon(),
                student.getTasks()
                        .stream()
                        .sorted((t1, t2) -> t2.getTaskReference().getTaskIndex() - t1.getTaskReference().getTaskIndex())
                        .map(taskMapper::fromStudentTaskToDTO)
                        .toList()
        );
    }

    @Override
    public Specification<Course> buildSpecificationFromFilters(FilterForm filters) {

        log.info("Building specification from filters: " + filters);

        String name = filters.getName();
        String dateString = filters.getDate();
        String beginDateString = filters.getBegin();
        String endDateString = filters.getEnd();
        Long mentorID = filters.getMentor();
        Long managerID = filters.getManager();
        String status = filters.getStatus();

        CourseStatus courseStatus = (status == null || status.isEmpty()) ? null : CourseStatus.valueOf(status);

        Admin mentor = (mentorID == null) ? null : adminRepository.getReferenceById(mentorID);
        Admin manager = (managerID == null) ? null : adminRepository.getReferenceById(managerID);

        LocalDate beginDate = (beginDateString != null && !beginDateString.isEmpty()) ? LocalDate.parse(beginDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null;
        LocalDate endDate = (endDateString != null && !endDateString.isEmpty()) ? LocalDate.parse(endDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null;

//        LocalDate beginDate = null;
//        LocalDate endDate = null;
//        if(dateString != null && !dateString.isEmpty()) {
//            try {
//                String[] dates = dateString.split(" - ");
//                beginDate = LocalDate.parse(dates[0], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//                endDate = LocalDate.parse(dates[1], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//            } catch (Exception e) {
//                log.warning("Error during date parsing");
//            }
//        }

        Specification<Course> spec = Specification.where(
                        CourseSpecifications.hasNameLike(name)
                        .and(CourseSpecifications.hasMentor(mentor))
                        .and(CourseSpecifications.hasManager(manager))
                        .and(CourseSpecifications.hasStatus(courseStatus)
                        .and(CourseSpecifications.hasDatesBetween(beginDate, endDate)))
        );
        return spec;
    }

    @Override
    public Course getCourseById(Long id) {
        return courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course not found"));
    }

    private void updateCourseStudents(Course c, CourseEditDTO dto) {

        Set<Student> courseStudents = c.getStudents();
        courseStudents.forEach(student -> student.setCourse(null));
        courseStudents.clear();

        dto.getMembers().getStudents().forEach(student -> {
            studentRepository.findById(student.getId()).ifPresent(foundStudent -> {
                foundStudent.setCourse(c);
                Student st = studentRepository.save(foundStudent);
                courseStudents.add(st);
            });
        });
    }

    private void updateCourseTaskList(Course c, CourseEditDTO dto) {
        List<Task> oldTaskList = taskRepository.getCourseTasks(c.getId());
        log.info("old task list: {}", oldTaskList.stream().map(Task::getId).toList());
        oldTaskList.forEach(task -> {task.setCourse(null); taskRepository.save(task);});

        log.info(dto.getStructure().getTasks().toString());

        dto.getStructure().getTasks().forEach(newCourseTask -> {
            taskRepository.findById(newCourseTask.getId()).ifPresent(foundTask -> {
                foundTask.setTaskIndex(newCourseTask.getTaskIndex());
                foundTask.setCourse(c);
                taskRepository.save(foundTask);
            });
        });
        List<Task> newTaskList = taskRepository.getCourseTasks(c.getId());
        log.info("new task list: {}", newTaskList.stream().map(Task::getId).toList());
    }

}
