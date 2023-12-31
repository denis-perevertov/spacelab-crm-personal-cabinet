package com.example.spacelab.service.specification;

import com.example.spacelab.model.admin.Admin;
import com.example.spacelab.model.course.Course;
import com.example.spacelab.model.role.UserRole;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class AdminSpecifications {

    public static Specification<Admin> hasId(Long id) {
        if(id == null) return (root, query, cb) -> null;
        return (root, query, cb) -> cb.equal(root.get("id"), id);
    }

    public static Specification<Admin> hasNameLike(String input) {
        if(input == null) return (root, query, cb) -> null;
        return (root, query, cb) ->
                cb.or(
                        cb.like(root.get("firstName"), "%"+input+"%"),
                        cb.like(root.get("lastName"), "%"+input+"%")
                );
    }

    public static Specification<Admin> hasRole(UserRole role) {
        if(role == null) return (root, query, cb) -> null;
        return (root, query, cb) -> cb.equal(root.get("role"), role);
    }

    public static Specification<Admin> hasCourse(Course course) {
        if(course == null) return (root, query, cb) -> null;
        return (root, query, cb) -> cb.equal(root.get("courses").get("id"), course.getId());
    }

    public static Specification<Admin> hasDatesBetween(LocalDate from, LocalDate to) {
        if(from == null || to == null) return (root, query, cb) -> null;
        return (root, query, cb) -> cb.between(root.get("birthdate"), from, to);
    }

    public static Specification<Admin> hasEmailLike(String input) {
        if(input == null) return (root, query, cb) -> null;
        return (root, query, cb) -> cb.like(root.get("email"), "%"+input+"%");
    }

    public static Specification<Admin> hasPhoneLike(String phone) {
        if(phone == null) return (root, query, cb) -> null;
        return (root, query, cb) -> cb.like(root.get("phone"), "%"+phone+"%");
    }


}
