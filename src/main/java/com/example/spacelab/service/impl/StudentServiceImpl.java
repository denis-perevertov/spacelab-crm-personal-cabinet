package com.example.spacelab.service.impl;

import com.example.spacelab.dto.student.StudentRegisterRequest;
import com.example.spacelab.exception.ResourceNotFoundException;
import com.example.spacelab.mapper.StudentMapper;
import com.example.spacelab.mapper.TaskMapper;
import com.example.spacelab.model.course.Course;
import com.example.spacelab.model.lesson.LessonReportRow;
import com.example.spacelab.model.student.StudentInviteRequest;
import com.example.spacelab.model.student.Student;
import com.example.spacelab.model.student.StudentTask;
import com.example.spacelab.dto.student.StudentCardDTO;
import com.example.spacelab.model.task.Task;
import com.example.spacelab.repository.*;
import com.example.spacelab.service.FileService;
import com.example.spacelab.service.StudentService;
import com.example.spacelab.service.TaskService;
import com.example.spacelab.service.specification.StudentSpecifications;
import com.example.spacelab.util.FilterForm;
import com.example.spacelab.model.student.StudentAccountStatus;
import com.example.spacelab.model.student.StudentTaskStatus;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final InviteStudentRequestRepository inviteRepository;
    private final StudentTaskRepository studentTaskRepository;
    private final UserRoleRepository userRoleRepository;

    private final TaskService taskService;
    private final FileService fileService;

    private final StudentMapper studentMapper;
    private final TaskMapper taskMapper;

    private final PasswordEncoder passwordEncoder;

    @Override
    public Student getStudentById(Long id) {
        log.info("Getting student with ID: " + id);
        Student student = studentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Student not found", Student.class));
        return student;
    }

    @Override
    public Student registerStudent(Student student) {

        student.setRating(0);
        student.setRole(userRoleRepository.getReferenceByName("STUDENT"));
        student.getDetails().setAccountStatus(StudentAccountStatus.ACTIVE);

        student = studentRepository.save(student);
        log.info("Created student: " + student);
        return student;
    }

    @Override
    public Student registerStudent(StudentRegisterRequest request) throws IOException {
        log.info("registering student through request from website");
        Student student = studentMapper.fromRegisterRequestToStudent(request);
        student.setRating(0);
        student.setRole(userRoleRepository.getReferenceByName("STUDENT"));
        student.getDetails().setAccountStatus(StudentAccountStatus.ACTIVE);
        student.setPassword(passwordEncoder.encode(request.password()));
        if(request.avatar().getSize() > 0) {
            log.info("saving user avatar");
            fileService.saveFile(request.avatar(), "users");
            student.setAvatar("/uploads/users/" + request.avatar().getOriginalFilename());
        }
        return studentRepository.save(student);
    }

    @Override
    public StudentCardDTO getCard(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Student not found", Student.class));
        return studentMapper.fromStudentToCardDTO(student);
    }

    /*
     = = = = = Задания студента = = = = =
     */


//    @Override
//    public List<StudentTask> getStudentTasks(Long studentID) {
//        log.info("Getting tasks of student w/ ID: " + studentID);
//        return studentTaskRepository.findStudentTasks(studentID);
//    }
//
//    @Override
//    public List<StudentTask> getStudentTasks(Long studentID, StudentTaskStatus status) {
//        log.info("Getting tasks(STATUS:"+status.toString()+") of student w/ ID: " + studentID);
//        return studentTaskRepository.findStudentTasksWithStatus(studentID, status);
//    }
//
//    @Override
//    public Page<StudentTask> getStudentTasks(Long studentID, StudentTaskStatus status, Pageable pageable) {
//        log.info("Getting "+pageable.getPageSize()+" tasks(STATUS:"+status.toString()+")" +
//                " of student w/ ID: " + studentID +
//                " || page " + pageable.getPageNumber());
//        return studentTaskRepository.findStudentTasksWithStatusAndPage(studentID, status, pageable);
//    }
//
//    @Override
//    public Page<StudentTask> getStudentTasks(Specification<StudentTask> spec, Pageable pageable) {
//        return studentTaskRepository.findAll(spec, pageable);
//    }
//
//    @Override
//    public StudentTask getStudentTask(Long taskID) {
//        log.info("Getting student task with taskID: " + taskID);
//        StudentTask task = studentTaskRepository.findById(taskID).orElseThrow(() -> new ResourceNotFoundException("Student task not found", StudentTask.class));
//        return task;
//    }
//
//    @Override
//    public void createStudentTasksOnCourseTransfer(Student student, Course course) {
//
//        List<Task> courseTaskList = course.getTasks();
//
//        // clear student tasks which were not completed
//        List<StudentTask> oldStudentTasks = student.getTasks();
//        oldStudentTasks.stream()
//                .filter(studentTask -> studentTask.getStatus() != StudentTaskStatus.COMPLETED)
//                .forEach(studentTaskRepository::delete);
//
//        // create new task snapshots for student
//
//
//    }
//
//    @Override
//    public void completeStudentTask(Long taskID) {
//
//    }
    @Override
    public List<LessonReportRow> getStudentLessonData(Long studentID) {
        return getStudentById(studentID).getLessonData();
    }

    @Override
    public Long getStudentCourseID(Long studentID) {
        Course studentCourse = getStudentById(studentID).getCourse();
        return (studentCourse != null) ? studentCourse.getId() : null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return studentRepository.findByDetailsEmail(username).orElseThrow(() -> new EntityNotFoundException("Student not found!"));
    }
}
