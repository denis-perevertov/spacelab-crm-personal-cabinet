package com.example.spacelab.model.student;

import com.example.spacelab.model.UserEntity;
import com.example.spacelab.model.course.Course;
import com.example.spacelab.model.lesson.LessonReportRow;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name="students")
public class Student extends UserEntity {

    @Embedded
    private StudentDetails details = new StudentDetails();

    private String password;

    private Integer rating;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @OneToMany(mappedBy = "student")
    private List<StudentTask> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "student")
    private List<LessonReportRow> lessonData = new ArrayList<>();

    public String getFullName() {
        return String.join(" ",
                this.details.getFirstName(),
                this.details.getFathersName(),
                this.details.getLastName());
    }


}