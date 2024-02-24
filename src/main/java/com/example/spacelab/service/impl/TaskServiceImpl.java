package com.example.spacelab.service.impl;

import com.example.spacelab.dto.student.StudentTaskLessonDTO;
import com.example.spacelab.dto.task.StudentTaskPointDTO;
import com.example.spacelab.dto.task.StudentTaskTagDTO;
import com.example.spacelab.exception.ResourceNotFoundException;
import com.example.spacelab.integration.TaskTrackingService;
import com.example.spacelab.integration.data.TaskResponse;
import com.example.spacelab.mapper.TaskMapper;
import com.example.spacelab.model.course.Course;
import com.example.spacelab.model.student.Student;
import com.example.spacelab.model.student.StudentTask;
import com.example.spacelab.model.student.StudentTaskStatus;
import com.example.spacelab.model.task.Task;
import com.example.spacelab.model.task.TaskLevel;
import com.example.spacelab.model.task.TaskStatus;
import com.example.spacelab.repository.CourseRepository;
import com.example.spacelab.repository.StudentRepository;
import com.example.spacelab.repository.StudentTaskRepository;
import com.example.spacelab.repository.TaskRepository;
import com.example.spacelab.service.PDFService;
import com.example.spacelab.service.TaskService;
import com.example.spacelab.service.specification.StudentTaskSpecifications;
import com.example.spacelab.service.specification.TaskSpecifications;
import com.example.spacelab.util.FilterForm;
import com.example.spacelab.util.TranslationService;
import com.example.spacelab.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final StudentTaskRepository studentTaskRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final TaskMapper mapper;

    private final TaskTrackingService trackingService;
    private final PDFService pdfService;

    @Override
    public List<StudentTaskLessonDTO> getOpenStudentTasks(Student student) {
        return student.getTasks().stream()
                .filter(task -> task.getStatus().equals(StudentTaskStatus.UNLOCKED))
                .map(task -> new StudentTaskLessonDTO(
                        task.getId(),
                        task.getTaskReference().getTaskIndex(),
                        task.getTaskReference().getName(),
                        task.getStatus().name(),
                        task.getPercentOfCompletion()
                ))
                .toList();
    }

    // up to 3 next tasks
    @Override
    public List<StudentTaskLessonDTO> getNextStudentTasks(Student student) {
        return student.getTasks().stream()
                .filter(task -> task.getStatus().equals(StudentTaskStatus.LOCKED))
                .map(task -> new StudentTaskLessonDTO(
                        task.getId(),
                        task.getTaskReference().getTaskIndex(),
                        task.getTaskReference().getName(),
                        task.getStatus().name(),
                        task.getPercentOfCompletion()
                ))
                .limit(3)
                .toList();
    }

    @Override
    public List<StudentTaskPointDTO> getStudentTaskProgressPoints(Long taskId) {
        StudentTask st = getStudentTask(taskId);
        List<TaskResponse> response = trackingService.getAllTasksFromList(st.getTaskTrackingId());
        List<StudentTaskPointDTO> points = response.stream().map(this::toPointDTO).toList();
        points.forEach(point -> {
            if(point.getParentTaskId() != null && point.getParentTaskId() != 0) {
                points.stream()
                        .filter(p -> p.getId().equals(point.getParentTaskId()))
                        .findAny()
                        .get()
                        .getSubpoints()
                        .add(point);
            }
        });

        return points.stream()
                .filter(point -> (point.getParentTaskId() == null || point.getParentTaskId() == 0))
                .toList();
    }

    private StudentTaskPointDTO toPointDTO(TaskResponse task) {
        List<StudentTaskTagDTO> tags = new ArrayList<>();
        if(task.tagIds() != null && task.tagIds().length > 0) {
            for(Long tagId : task.tagIds()) {
                tags.add(
                        Optional.of(trackingService.getTagById(String.valueOf(tagId)))
                                .map(
                                        tag -> new StudentTaskTagDTO(
                                                tag.id(),
                                                tag.name(),
                                                tag.color()
                                        )
                                ).get()
                );
            }
        }
        return new StudentTaskPointDTO(
                task.id(),
                task.parentTaskId(),
                task.tasklistId(),
                task.name(),
                task.description(),
                task.priority(),
                task.progress(),
                task.startDate(),
                task.status(),
                trackingService.getTotalTimeOnTask(String.valueOf(task.id())).taskTimeTotal().minutes(),
                trackingService.getTotalTimeOnTask(String.valueOf(task.id())).taskTimeTotal().estimatedMinutes(),
                tags,
                new ArrayList<>());
    }

    @Override
    public List<Task> getTasks() {
        return taskRepository.findAll();
    }

    public List<Student> getTaskStudents(Long taskId) {
        return getTaskById(taskId).getActiveStudents();
    }

    public Page<Task> getTasks(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }

    @Override
    public Page<Task> getTasks(FilterForm filters, Pageable pageable) {
        Specification<Task> spec = buildSpecificationFromFilters(filters);
        return taskRepository.findAll(spec, pageable);
    }

    public List<Task> getTasksByAllowedCourses(Long... ids) {
        return taskRepository.findAllByAllowedCourse(ids);
    }

    public Page<Task> getTasksByAllowedCourses(Pageable pageable, Long... ids) {
        return taskRepository.findAllByAllowedCoursePage(pageable, ids);
    }

    public Page<Task> getTasksByAllowedCourses(FilterForm filters, Pageable pageable, Long... ids) {
        Specification<Task> spec = buildSpecificationFromFilters(filters).and(TaskSpecifications.hasCourseIDs(ids));
        return taskRepository.findAll(spec, pageable);
    }

    @Override
    public Task getTaskById(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        return task;
    }

    public Task createNewTask(Task taskIn) {
        Task task = taskRepository.save(taskIn);
        return task;
    }

    public Task editTask(Task taskIn) {
        Task task = taskRepository.save(taskIn);
        return task;
    }

    @Transactional
    public void deleteTaskById(Long id) {
        Task task = taskRepository.findById(id).orElseThrow();
        taskRepository.findTasksByParentTask(task).forEach(subtask -> {
            subtask.setParentTask(null);
            subtask.setStatus(TaskStatus.INACTIVE);
            taskRepository.save(subtask);
        });
        taskRepository.deleteById(id);
    }

    public StudentTask unlockTaskForStudent(Long taskID, Long studentID) {
        Student student = studentRepository.findById(studentID).orElseThrow();
        Task originalTask = getTaskById(taskID);
        log.info("Unlocking Task (ID: {}) for Student (ID: {})", taskID, studentID);
        StudentTask studentTask = new StudentTask();
        studentTask.setStudent(student);
        studentTask.setTaskReference(originalTask);
        studentTask.setSubtasks(new ArrayList<>());
        studentTask.setBeginDate(ZonedDateTime.now());
        studentTask.setStatus(StudentTaskStatus.UNLOCKED);
        studentTask.setPercentOfCompletion(0);
        studentTask = studentTaskRepository.save(studentTask);
        originalTask.getActiveStudents().add(student);
        taskRepository.save(originalTask);
        log.info("Created Student Copy of Task (ID: {}): {}", taskID, studentTask);
        return studentTask;
    }



    public List<Task> getTaskSubtasks(Long id) {
        return taskRepository.findTaskSubtasks(id);
    }

    public Page<Task> getAvailableTasks(Pageable pageable) {
        Page<Task> page = taskRepository.findAvailableParentTasks(pageable);
        log.info("found available tasks: {}", page);
        return page;
    }

    public List<StudentTask> getStudentTasks(Long studentID) {
        log.info("Getting tasks of student w/ ID: " + studentID);
        return studentTaskRepository.findStudentTasks(studentID);
    }

    public List<StudentTask> getStudentTasks(Long studentID, StudentTaskStatus status) {
        log.info("Getting tasks(STATUS:"+status.toString()+") of student w/ ID: " + studentID);
        return studentTaskRepository.findStudentTasksWithStatus(studentID, status);
    }

    public Page<StudentTask> getStudentTasks(Long studentID, StudentTaskStatus status, Pageable pageable) {
        log.info("Getting "+pageable.getPageSize()+" tasks(STATUS:"+status.toString()+")" +
                " of student w/ ID: " + studentID +
                " || page " + pageable.getPageNumber());
        return studentTaskRepository.findStudentTasksWithStatusAndPage(studentID, status, pageable);
    }

    public Page<StudentTask> getStudentTasks(Specification<StudentTask> spec, Pageable pageable) {
        return studentTaskRepository.findAll(spec, pageable);
    }

    @Override
    public Page<StudentTask> getStudentTasks(FilterForm filters, Pageable pageable) {
        Specification<StudentTask> spec = studentTaskSpecification(filters);
        return studentTaskRepository.findAll(spec, pageable);
    }

    @Override
    public StudentTask getStudentTask(Long taskID) {
        log.info("Getting student task with taskID: " + taskID);
        StudentTask task = studentTaskRepository.findById(taskID).orElseThrow(() -> new ResourceNotFoundException("Student task not found", StudentTask.class));
        return task;
    }

    @Override
    public void createStudentTasksOnCourseTransfer(Student student, Course course) {

        // clear student tasks which were not completed
        List<StudentTask> oldStudentTasks = new ArrayList<>(student.getTasks().stream().toList());
        oldStudentTasks.stream()
                .filter(studentTask -> studentTask.getStatus() != StudentTaskStatus.COMPLETED)
                .forEach(studentTaskRepository::delete);

        List<Task> taskReferences = oldStudentTasks.stream().map(StudentTask::getTaskReference).toList();

        // create new task snapshots for student (not saved in db yet)
        List<StudentTask> newStudentTasks = createStudentTaskListFromCourse(course);
        newStudentTasks.forEach(newTask -> {
            if(!taskReferences.contains(newTask.getTaskReference())) {
                // save any new student task to db
                newTask.setStudent(student);
                newTask = studentTaskRepository.save(newTask);
                oldStudentTasks.add(newTask);
            }
        });

    }

    @Override
    public List<StudentTask> createStudentTaskListFromCourse(Course course) {
        return course.getTasks().stream().map(this::fromTaskToStudentTask).toList();
    }

    @Override
    public File generatePDF(Long taskId, String localeCode) throws IOException, URISyntaxException {
        Task task = getStudentTask(taskId).getTaskReference();
        return pdfService.generatePDF(task, TranslationService.getLocale(localeCode));
    }

    public StudentTask fromTaskToStudentTask(Task task) {
        // base case to exit recursion
        if(task == null) return null;

        StudentTask st = new StudentTask();
        st.setTaskReference(task);
        st.setParentTask(fromTaskToStudentTask(task.getParentTask()));
        st.setPercentOfCompletion(0);
        st.setStatus(StudentTaskStatus.LOCKED);
        task.getSubtasks().forEach(subtask -> st.getSubtasks().add(fromTaskToStudentTask(subtask)));

        return st;
    }

    @Override
    public void completeStudentTask(Long taskID) {
        StudentTask task = studentTaskRepository.findById(taskID).orElseThrow();
        task.getTaskReference().getActiveStudents().remove(task.getStudent());
        task.setStatus(StudentTaskStatus.COMPLETED);
        task.setEndDate(ZonedDateTime.now());
        studentTaskRepository.save(task);
        log.info("task completed");
    }

    @Override
    public void markStudentTaskAsReady(Long taskID) {
        StudentTask task = studentTaskRepository.findById(taskID).orElseThrow();
        task.setStatus(StudentTaskStatus.READY);
        studentTaskRepository.save(task);
        log.info("task {} set as ready", taskID);
    }

    @Override
    public void markStudentTaskAsNotReady(Long taskID) {
        StudentTask task = studentTaskRepository.findById(taskID).orElseThrow();
        task.setStatus(StudentTaskStatus.UNLOCKED);
        studentTaskRepository.save(task);
        log.info("task {} set as not ready", taskID);
    }


    @Override
    public Specification<Task> buildSpecificationFromFilters(FilterForm filters) {
        log.info("Building specification from filters: " + filters);

        String name = filters.getName();
        Long courseID = filters.getCourse();
        String levelInput = filters.getLevel();
        String statusInput = filters.getStatus();

        Course course = (courseID == null || courseID <= 0) ? null : courseRepository.getReferenceById(courseID);
        TaskStatus status = (statusInput == null || statusInput.isEmpty()) ? null : TaskStatus.valueOf(statusInput);
        TaskLevel level = (levelInput == null || levelInput.isEmpty()) ? null : TaskLevel.valueOf(levelInput);

        Specification<Task> spec = Specification.where(TaskSpecifications.hasNameLike(name))
                .and(TaskSpecifications.hasCourse(course))
                .and(TaskSpecifications.hasLevel(level))
                .and(TaskSpecifications.hasStatus(status));

        return spec;
    }

    private Specification<StudentTask> studentTaskSpecification(FilterForm filters) {
        Long id = filters.getId();
        Long student = filters.getStudent();
        String name = filters.getName();
        Long courseID = Optional.ofNullable(filters.getCourse()).orElse(-1L);
        String statusInput = filters.getStatus();
        String beginDateString = filters.getBegin();
        String endDateString = filters.getEnd();
        // todo add dates

        StudentTaskStatus status = statusInput == null ? StudentTaskStatus.UNLOCKED : StudentTaskStatus.valueOf(statusInput);

        ZonedDateTime from, to;
        from = ValidationUtils.fieldIsEmpty(beginDateString) ? null : LocalDate.parse(beginDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay().atZone(ZoneId.of("UTC"));
        to = ValidationUtils.fieldIsEmpty(endDateString) ? null : LocalDate.parse(endDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atTime(LocalTime.MAX).atZone(ZoneId.of("UTC"));

        Specification<StudentTask> spec =
                StudentTaskSpecifications.hasCourseID(courseID <= 0 ? null : courseID)
                        .and(StudentTaskSpecifications.hasDatesBetween(from, to))
                        .and(StudentTaskSpecifications.hasStudentId(student))
                        .and(StudentTaskSpecifications.hasNameLike(name))
                        .and(StudentTaskSpecifications.hasId(id))
                        .and(StudentTaskSpecifications.hasStatus(status));

        return spec;
    }
}
