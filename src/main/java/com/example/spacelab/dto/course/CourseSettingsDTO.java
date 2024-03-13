package com.example.spacelab.dto.course;

import com.example.spacelab.util.TimeUnit;

import java.time.LocalDate;

public record CourseSettingsDTO(
        LocalDate beginDate,
        String completionTime,
        TimeUnit completionTimeUnit,
        Integer groupSize,
        Integer hoursNorm,
        Integer lessonInterval
) {

}
