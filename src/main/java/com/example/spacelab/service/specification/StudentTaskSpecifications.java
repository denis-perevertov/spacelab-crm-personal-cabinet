package com.example.spacelab.service.specification;

import com.example.spacelab.model.student.StudentTask;
import com.example.spacelab.model.student.StudentTaskStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.ZonedDateTime;

public class StudentTaskSpecifications {

    public static Specification<StudentTask> hasId(Long id) {
        if(id == null) return (root, query, cb) -> null;
        return (root, query, cb) -> cb.equal(root.get("id"), id);
    }

    public static Specification<StudentTask> hasStudentId(Long id) {
        if(id == null) return (root, query, cb) -> null;
        return (root, query, cb) -> cb.equal(root.get("student").get("id"), id);
    }

    public static Specification<StudentTask> hasNameLike(String name) {
        if(name == null) return (root, query, cb) -> null;
        return (root, query, cb) -> cb.like(root.get("taskReference").get("name"), "%"+name+"%");
    }

    public static Specification<StudentTask> hasCourseID(Long id) {
        if(id == null || id < 0) return (root, query, cb) -> null;
        return (root, query, cb) -> cb.equal(root.get("taskReference").get("course").get("id"), id);
    }

    public static Specification<StudentTask> hasDatesBetween(ZonedDateTime from, ZonedDateTime to) {
        if(from == null && to == null) return (root, query, cb) -> null;
        else if(from != null && to != null) return (root, query, cb) -> cb.or(
                cb.and(cb.greaterThanOrEqualTo(root.get("beginDate"), from),
                        cb.lessThanOrEqualTo(root.get("endDate"), from)),
                cb.and(cb.greaterThanOrEqualTo(root.get("beginDate"), to),
                        cb.lessThanOrEqualTo(root.get("endDate"), to)),
                cb.and(cb.greaterThanOrEqualTo(root.get("beginDate"), from),
                        cb.lessThanOrEqualTo(root.get("endDate"), to))
        );
        else if(from != null) return (root, query, cb) -> cb.and(
                cb.greaterThanOrEqualTo(root.get("beginDate"), from),
                cb.lessThanOrEqualTo(root.get("endDate"), from)
        );
        else return (root, query, cb) -> cb.and(
                    cb.greaterThanOrEqualTo(root.get("beginDate"), to),
                    cb.lessThanOrEqualTo(root.get("endDate"), to)
            );
    }

    public static Specification<StudentTask> hasStatus(StudentTaskStatus status) {
        if(status == null) return (root, query, cb) -> null;
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }
}
