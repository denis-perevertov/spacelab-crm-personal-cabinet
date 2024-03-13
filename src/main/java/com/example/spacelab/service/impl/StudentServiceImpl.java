package com.example.spacelab.service.impl;

import com.example.spacelab.dto.student.*;
import com.example.spacelab.exception.ResourceNotFoundException;
import com.example.spacelab.exception.TeamworkException;
import com.example.spacelab.integration.TaskTrackingService;
import com.example.spacelab.integration.data.*;
import com.example.spacelab.mapper.StudentMapper;
import com.example.spacelab.mapper.TaskMapper;
import com.example.spacelab.model.course.Course;
import com.example.spacelab.model.lesson.Lesson;
import com.example.spacelab.model.lesson.LessonReportRow;
import com.example.spacelab.model.lesson.LessonStatus;
import com.example.spacelab.model.student.*;
import com.example.spacelab.repository.*;
import com.example.spacelab.service.FileService;
import com.example.spacelab.service.StudentService;
import com.example.spacelab.service.TaskService;
import com.example.spacelab.util.FilenameUtils;
import com.example.spacelab.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final InviteStudentRequestRepository inviteRepository;
    private final StudentTaskRepository studentTaskRepository;
    private final UserRoleRepository userRoleRepository;
    private final LessonReportRowRepository lessonReportRowRepository;

    private final TaskService taskService;
    private final FileService fileService;

    private final TaskTrackingService trackingService;

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
    @Transactional
    public Student registerStudent(StudentRegisterRequest request) throws IOException {
        log.info("registering student through request from website");
        Student student = studentMapper.fromRegisterRequestToStudent(request);
        student.setRating(0);
        student.setRole(userRoleRepository.getReferenceByName("STUDENT"));
        student.getDetails().setAccountStatus(StudentAccountStatus.ACTIVE);
        student.setPassword(passwordEncoder.encode(request.password()));
        if(request.avatar().getSize() > 0) {
            log.info("saving user avatar");
            String filename = FilenameUtils.generateFileName(request.avatar());
            fileService.saveFile(request.avatar(), filename, "students");
            student.setAvatar(filename);
        }
        Student savedStudent = studentRepository.save(student);
        log.info("registered & saved user");
        inviteRepository.deleteByToken(request.inviteToken());
        log.info("deleted used token");
        try {
            createTrackingUserProfile(savedStudent);
            if(
                    ValidationUtils.fieldIsNotEmpty(savedStudent.getTaskTrackingProfileId())
                    && ValidationUtils.fieldIsNotNull(savedStudent.getCourse())
                    && ValidationUtils.fieldIsNotEmpty(savedStudent.getCourse().getTrackingId())
            ) {
                addStudentToProject(
                        savedStudent.getTaskTrackingProfileId(),
                        savedStudent.getCourse().getTrackingId()
                );
            }

        } catch (TeamworkException ex) {
            log.error("could not create teamwork user: {}", ex.getMessage());
        }
        return savedStudent;
    }

    @Async
    private void createTrackingUserProfile(Student savedStudent) {
        log.info("creating tracking profile for user");
        UserResponse trackingUserResponse = trackingService.createTaskUser(new UserCreateRequest(
                savedStudent.getDetails().getEmail(),
                savedStudent.getDetails().getFirstName(),
                savedStudent.getDetails().getLastName(),
                false,
                "Student",
                savedStudent.getDetails().getGithubLink(),
                savedStudent.getDetails().getLinkedinLink(),
                null,
                false,
                false,
                false,
                false,
                "account"
        ));
        savedStudent.setTaskTrackingProfileId(trackingUserResponse.id());
        studentRepository.save(savedStudent);
    }

    @Async
    private void addStudentToProject(String studentId, String projectId) {
        log.info("Adding student(id:{}) to project(id:{})", studentId, projectId);
        UserAddResponse response = trackingService.addUsersToProject(new UserAddRequest(projectId, new Integer[]{Integer.parseInt(studentId)}));
        log.info(response.toString());
    }

    @Async
    private void removeStudentFromProject(String studentId, String projectId) {
        log.info("Removing student(id:{}) from project(id:{})", studentId, projectId);
        UserRemoveResponse response = trackingService.removeUsersFromProject(new UserRemoveRequest(projectId, new UserRemoveRequest.Remove(
                studentId
        )));
        log.info(response.toString());
    }

    @Override
    public StudentCardDTO getCard(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Student not found", Student.class));
        return studentMapper.fromStudentToCardDTO(student);
    }

    @Override
    public TimeTotalResponse getStudentTotalLearningTime(Long studentId) {
        Student st = getStudentById(studentId);
        if(ValidationUtils.fieldIsEmpty(st.getTaskTrackingProfileId())) {
            return null;
        }
        else {
            return trackingService.getUserTotalTime(st.getTaskTrackingProfileId());
        }
    }

    @Override
    public TimeTotalResponse getStudentRecentLearningTime(Long studentId) {
        Student st = getStudentById(studentId);
        if(ValidationUtils.fieldIsEmpty(st.getTaskTrackingProfileId())) {
            return null;
        }
        else {
            return trackingService.getUserTotalTimeRecent(st.getTaskTrackingProfileId());
        }
    }

    @Override
    public Optional<StudentTask> getStudentLastCompletedTask(Long studentId) {
        Student st = getStudentById(studentId);
        return st.getTasks()
                .stream()
                .filter(t -> t.getStatus().equals(StudentTaskStatus.COMPLETED))
                .sorted(Comparator.comparing(StudentTask::getEndDate).reversed())
                .limit(1)
                .findAny();
    }

    @Override
    public long getStudentCompletedTaskAmount(Long studentId) {
        Student st = getStudentById(studentId);
        return st.getTasks()
                .stream()
                .filter(t -> t.getStatus().equals(StudentTaskStatus.COMPLETED))
                .count();
    }

    @Override
    public Optional<Lesson> getStudentLastVisitedLesson(Long studentId) {
        Student st = getStudentById(studentId);
        return lessonRepository.findAllByCourse(st.getCourse())
                .stream()
                .filter(l -> l.getStatus().equals(LessonStatus.COMPLETED))
                .sorted(Comparator.comparing(Lesson::getDatetime).reversed())
                .limit(1)
                .findAny();
    }

    @Override
    public Optional<Lesson> getStudentNextLesson(Long studentId) {
        Student st = getStudentById(studentId);
        return lessonRepository.findAllByCourse(st.getCourse())
                .stream()
                .filter(l -> l.getStatus().equals(LessonStatus.PLANNED))
                .sorted(Comparator.comparing(Lesson::getDatetime))
                .limit(1)
                .findAny();
    }

    // get lesson report row
    @Override
    public long getStudentVisitedLessonAmount(Long studentId) {
        Student st = getStudentById(studentId);
        return lessonReportRowRepository.findAllByStudent(st)
                .stream()
                .filter(LessonReportRow::getWasPresent)
                .count();
    }

    @Override
    public long getStudentSkippedLessonAmount(Long studentId) {
        Student st = getStudentById(studentId);
        return lessonReportRowRepository.findAllByStudent(st)
                .stream()
                .filter(r -> !r.getWasPresent())
                .count();
    }

    @Override
    public void saveProfileDetails(Long studentId, StudentDetailsDTO dto) {
        Student st = getStudentById(studentId);
        StudentDetails details = st.getDetails();
        details.setBirthdate(dto.birthdate());
        details.setPhone(dto.phone());
        details.setEmail(dto.email());
        details.setEducationLevel(dto.educationLevel());
        details.setEnglishLevel(dto.englishLevel());
        details.setWorkStatus(dto.workStatus());
        studentRepository.save(st);
        log.info("profile details saved");
    }

    @Override
    public void saveStudentName(Long studentId, StudentNameDTO dto) {
        Student st = getStudentById(studentId);
        StudentDetails details = st.getDetails();
        details.setFirstName(dto.firstName());
        details.setFathersName(dto.fathersName());
        details.setLastName(dto.lastName());
        studentRepository.save(st);
        log.info("profile name saved");
    }

    @Override
    public String saveStudentAvatar(Long studentId, StudentAvatarEditRequest request) throws IOException {
        Student st = getStudentById(studentId);
        log.info("saving user avatar");
        String filename = FilenameUtils.generateFileName(request.avatar());
        fileService.saveFile(request.avatar(), filename, "students");
        st.setAvatar(filename);
        studentRepository.save(st);
        log.info("user avatar saved");
        return filename;
    }

    @Override
    public List<LessonReportRow> getStudentLessonData(Long studentID) {
//        return getStudentById(studentID).getLessonData();
        return new ArrayList<>();
    }

    @Override
    public Long getStudentCourseID(Long studentID) {
        Course studentCourse = getStudentById(studentID).getCourse();
        return (studentCourse != null) ? studentCourse.getId() : null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return studentRepository.findByDetailsEmail(username).orElseThrow(() -> new UsernameNotFoundException("Student not found!"));
    }
}
