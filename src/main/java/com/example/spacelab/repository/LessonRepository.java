package com.example.spacelab.repository;

import com.example.spacelab.model.course.Course;
import com.example.spacelab.model.lesson.Lesson;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Hidden
@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long>, JpaSpecificationExecutor<Lesson> {

    @Query("SELECT l FROM Lesson l WHERE l.course.id IN :ids")
    List<Lesson> findAllByAllowedCourse(@Param("ids") Long... ids);

    @Query("SELECT l FROM Lesson l WHERE l.course.id IN :ids")
    Page<Lesson> findAllByAllowedCoursePage(Pageable pageable, @Param("ids") Long... ids);

    List<Lesson> findAllByCourse(Course course);
}
