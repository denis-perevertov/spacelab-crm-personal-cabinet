package com.example.spacelab.util;

import com.example.spacelab.model.admin.Admin;
import com.example.spacelab.model.course.Course;
import com.example.spacelab.model.role.PermissionType;
import com.example.spacelab.model.student.Student;
import com.example.spacelab.repository.AdminRepository;
import com.example.spacelab.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Component
@Log
@RequiredArgsConstructor
@Transactional
public class AuthUtil {

    private final StudentRepository studentRepository;

    public static Student getLoggedInPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.isAuthenticated() && Objects.nonNull(authentication.getPrincipal())) {
            Object principal = authentication.getPrincipal();
            if(principal instanceof Student st) {
                return st;
            }
            else throw new EntityNotFoundException("No logged in user found");
        }
        else throw new EntityNotFoundException("No logged in user found");
    }

}
