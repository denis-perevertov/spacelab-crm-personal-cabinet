package com.example.spacelab.model.student;

import com.example.spacelab.model.UserEntity;
import com.example.spacelab.model.course.Course;
import com.example.spacelab.model.lesson.LessonReportRow;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Entity
@Table(name="students")
public class Student extends UserEntity implements UserDetails {

    @Embedded
    private StudentDetails details = new StudentDetails();

    private String password;

    private Integer rating;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ToString.Exclude
    @JsonManagedReference
    @OneToMany(mappedBy = "student")
    private List<StudentTask> tasks = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "student")
    private List<LessonReportRow> lessonData = new ArrayList<>();

    public String getFullName() {
        return String.join(" ",
                this.details.getFirstName(),
                this.details.getFathersName(),
                this.details.getLastName());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.getRole().getAuthorities().stream().map(SimpleGrantedAuthority::new).toList();
    }

    @Override
    public String getUsername() {
        return this.details.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
