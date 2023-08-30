package com.example.spacelab.repository;

import com.example.spacelab.model.student.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {

    @Query("SELECT s FROM Student s WHERE s.course.id IN :ids")
    List<Student> findAllByAllowedCourse(@Param("ids") Long... ids);

    @Query("SELECT s FROM Student s WHERE s.course.id IN :ids")
    Page<Student> findAllByAllowedCoursePage(Pageable pageable, @Param("ids") Long... ids);

    boolean existsByDetailsEmail(String email);
}
