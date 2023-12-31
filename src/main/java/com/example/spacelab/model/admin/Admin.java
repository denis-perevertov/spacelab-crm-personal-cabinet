package com.example.spacelab.model.admin;

import com.example.spacelab.model.contact.ContactInfo;
import com.example.spacelab.model.course.Course;
import com.example.spacelab.model.UserEntity;
import com.example.spacelab.model.settings.Settings;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import com.example.spacelab.model.lesson.Lesson;
import com.example.spacelab.model.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name="admins")
public class Admin extends UserEntity {

    @Column(name = "first_name")
    private String firstName;
    private String lastName;

    private String phone;
    private String email;

    private String password;
    @Transient private String confirmPassword;

    @ToString.Exclude
    @ManyToMany
    private List<Course> courses = new ArrayList<>();

    @ToString.Exclude
    @OneToMany
    private List<Lesson> lessons = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy="admin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContactInfo> contacts = new ArrayList<>();

    @ToString.Exclude
    @JsonIgnore
    @OneToOne(mappedBy = "admin")
    private Settings settings;

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

}
