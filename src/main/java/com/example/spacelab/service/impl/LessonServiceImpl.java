package com.example.spacelab.service.impl;

import com.example.spacelab.dto.student.StudentLessonDisplayDTO;
import com.example.spacelab.exception.LessonException;
import com.example.spacelab.exception.ResourceNotFoundException;
import com.example.spacelab.job.LessonMonitor;
import com.example.spacelab.model.admin.Admin;
import com.example.spacelab.model.course.Course;
import com.example.spacelab.model.lesson.Lesson;
import com.example.spacelab.model.lesson.LessonStatus;
import com.example.spacelab.model.student.Student;
import com.example.spacelab.model.task.Task;
import com.example.spacelab.model.task.TaskLevel;
import com.example.spacelab.model.task.TaskStatus;
import com.example.spacelab.repository.AdminRepository;
import com.example.spacelab.repository.CourseRepository;
import com.example.spacelab.repository.LessonRepository;
import com.example.spacelab.service.LessonService;
import com.example.spacelab.service.TaskService;
import com.example.spacelab.service.specification.LessonSpecifications;
import com.example.spacelab.service.specification.TaskSpecifications;
import com.example.spacelab.util.AuthUtil;
import com.example.spacelab.util.FilterForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final TaskService taskService;
    private final CourseRepository courseRepository;
    private final AdminRepository adminRepository;
    private final LessonRepository lessonRepository;

    @Override
    public List<Lesson> getLessons() {
        return lessonRepository.findAll();
    }

    @Override
    public List<Lesson> getStudentLessons() {
        Course course = AuthUtil.getLoggedInPrincipal().getCourse();
        log.info("Getting lessons for course of currently logged in student: " + course.getName());
        return lessonRepository.findAllByCourse(course);
    }

    @Override
    public Lesson getLessonById(Long id) {
        Lesson lesson = lessonRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));
        return lesson;
    }

    @Override
    public void startLesson(Long id) {
        Lesson lesson = getLessonById(id);
        if(!lesson.getStatus().equals(LessonStatus.PLANNED)) throw new LessonException("Can't start an active/completed lesson!");
        lesson.setStatus(LessonStatus.ACTIVE);
        lessonRepository.save(lesson);
    }

    @Override
    public List<StudentLessonDisplayDTO> getStudentLessonDisplayData(Long id) {
        Lesson lesson = getLessonById(id);
        List<Student> courseStudents = lesson.getCourse().getStudents();
        List<StudentLessonDisplayDTO> lessonDisplayData = new ArrayList<>();
        courseStudents.forEach(st -> lessonDisplayData.add(new StudentLessonDisplayDTO(
                st.getId(),
                st.getFullName(),
                10.00,
                taskService.getOpenStudentTasks(st),
                taskService.getNextStudentTasks(st)
        )));
        return lessonDisplayData;
    }

    @Override
    public Specification<Lesson> buildSpecificationFromFilters(FilterForm filters) {

        log.info("Building specification from filters: " + filters);

        String begin = filters.getBegin();
        String end = filters.getEnd();
        Long courseID = filters.getCourse();
        String statusInput = filters.getStatus();
        Long mentorID = filters.getMentor();
        Long managerID = filters.getManager();

        LocalDate beginDate = null, endDate = null;

        if(begin != null && !begin.isEmpty() && end != null && !end.isEmpty()) {
            beginDate = LocalDate.parse(begin, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            endDate = LocalDate.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        Course course = (courseID == null) ? null : courseRepository.getReferenceById(courseID);
        LessonStatus status = (statusInput == null) ? null : LessonStatus.valueOf(statusInput);
        Admin mentor = (mentorID == null) ? null : adminRepository.findById(mentorID).orElseThrow();
        Admin manager = (managerID == null) ? null : adminRepository.findById(managerID).orElseThrow();

        Specification<Lesson> spec = LessonSpecifications.hasDatesBetween(beginDate, endDate)
                                    .and(LessonSpecifications.hasCourse(course))
                                    .and(LessonSpecifications.hasStatus(status))
                                    .and(LessonSpecifications.hasManager(manager))
                                    .and(LessonSpecifications.hasMentor(mentor));

        return spec;
    }
}
