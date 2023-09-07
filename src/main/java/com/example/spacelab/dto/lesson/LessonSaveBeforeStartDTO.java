package com.example.spacelab.dto.lesson;

import com.example.spacelab.model.lesson.LessonStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class LessonSaveBeforeStartDTO {

    private Long id;

    private LocalDate date;
    private LocalTime time;

    private Long courseID;

    private Long mentorID;

    private Long managerID;

    private LessonStatus status;

    private String link;

    private Boolean startsAutomatically;
}
